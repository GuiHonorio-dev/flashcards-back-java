# Refresh Token — anotações de estudo

Documento de revisão: explica o que foi implementado, por quê, e como as peças se
encaixam. Ler isso deve ser suficiente pra reconstruir o entendimento do fluxo
inteiro sem precisar reler o código linha a linha.

## O problema que isso resolve

Access token (JWT) tem vida curta (`jwt.expiration = 900000` ms = 15 min) — de
propósito, porque se vazar, o estrago é limitado. Mas se o usuário precisasse
logar de novo a cada 15 min, a experiência seria péssima. O refresh token é um
token de vida **longa** (`jwt.refresh-expiration = 1209600000` ms = 14 dias) que
serve só pra uma coisa: pedir um novo access token sem precisar de email/senha
de novo.

## As duas naturezas de token

- **Access token**: JWT de verdade (assinado com HMAC, `TokenProvider`),
  **stateless** — o servidor não guarda nada dele, só valida assinatura +
  expiração a cada requisição.
- **Refresh token**: **não é JWT**. É só um UUID aleatório opaco
  (`tokenProvider.generateRefreshToken()` → `UUID.randomUUID().toString()`).
  Ele é **stateful**: existe uma linha no banco (`refresh_tokens`) representando
  ele, e é essa linha que pode ser consultada/revogada. Por isso dá pra "matar"
  uma sessão de refresh a qualquer momento (coisa que não dá pra fazer com um
  JWT stateless sem uma blocklist).

## Peça por peça

### 1. `model/RefreshToken.java` (entidade JPA)
Campos: `id` (UUID gerado), `student` (`@ManyToOne`), `tokenHash` (nunca o token
puro — igual senha, só o hash fica salvo), `expiresAt`, `revoked` (default
`false`). Tabela criada na migration `V6__create_refresh_token_table.sql`.

### 2. `repository/IRefreshTokenRepository.java`
Um método: `findByTokenHashAndRevokedFalse(String tokenHash)`. O filtro
`RevokedFalse` já embutido na query evita ter que checar `revoked` manualmente
depois de buscar.

### 3. `config/TokenProvider.java`
- `generateRefreshToken()` — gera o UUID opaco.
- `hashToken(String token)` — SHA-256 + Base64. Mesma ideia de nunca guardar
  segredo em texto puro no banco.
- `buildToken(Student student)` — **foi tornado público** (era `private`)
  porque o fluxo de refresh precisa emitir um access token novo a partir de um
  `Student` que já se sabe autenticado (via refresh token válido), sem ter um
  `Authentication` do Spring Security disponível (não houve login de novo).
  `gerarToken(Authentication)` continua existindo e por baixo dos panos chama
  esse mesmo `buildToken`.

### 4. `dto/Auth/TokenPairDto.java`
`record TokenPairDto(String accessToken, String refreshToken)` — carrega os
dois tokens juntos entre `AuthenticationService` e `AuthController`.

### 5. `service/CookieService.java`
Generalizado pra não ter mais nome de cookie/path fixos — hoje serve tanto
pro cookie de access quanto de refresh:
```java
buildCookie(String cookieName, String token, Duration maxAge, String path)
extractTokenFromCookie(HttpServletRequest request, String cookieName)
```
O campo `@Value("${jwt.cookie-name}")` continua existindo na classe mas não é
mais usado dentro dela mesma — quem injeta o nome certo do cookie em cada
chamada é quem chama o serviço (`AuthController`, `JwtAuthenticationFilter`).

### 6. `service/AuthenticationService.java`
- `login()` — autentica, gera access token (`gerarToken`) **e** refresh token
  (`generateRefreshToken` + `hashToken`), salva o hash no banco com
  `expiresAt = now + refreshExpirationTime`, retorna os dois tokens **puros**
  num `TokenPairDto` (só o hash fica persistido).
- `refresh(String rawRefreshToken)` — fluxo **sem rotação** (decisão
  consciente, ver seção abaixo):
  1. Hash do token recebido.
  2. Busca por `findByTokenHashAndRevokedFalse` → se não achar, token
     inválido/revogado → `InvalidRefreshTokenException`.
  3. Checa `expiresAt` → se passou, `InvalidRefreshTokenException`.
  4. Gera **novo access token** a partir de `stored.getStudent()`.
  5. Retorna `TokenPairDto(novoAccessToken, rawRefreshToken)` — o refresh
     token devolvido é **o mesmo** que chegou, não rotaciona.

### 7. `exception/InvalidRefreshTokenException.java` + `GlobalExceptionHandler`
Exceção customizada, seguindo o padrão já usado no projeto
(`EmailAlreadyExistsException`, `NotFoundException`, etc): uma exceção por
caso de erro, mapeada em `GlobalExceptionHandler` pro status HTTP certo
(`401 UNAUTHORIZED` nesse caso).

### 8. `controller/AuthController.java`
- `login()` — recebe `TokenPairDto`, monta **dois** cookies com
  `cookieService.buildCookie(...)`:
  - access: nome `jwt.cookie-name` (`session`), `path("/")`, expira em
    `jwt.expiration`.
  - refresh: nome `jwt.refresh-cookie-name` (`refresh_session`),
    `path("/v1/auth")`, expira em `jwt.refresh-expiration`.

  **Por que paths diferentes**: o cookie de refresh só precisa ser enviado
  pelo browser em rotas de auth (`/v1/auth/refresh`, `/v1/auth/logout`), não
  em toda requisição da API — restringir o `path` é defesa em profundidade
  (reduz a superfície onde esse cookie mais sensível circula).

- `logout()` — limpa os dois cookies (`Duration.ZERO`), **usando os mesmos
  paths** que foram usados pra criá-los. Isso importa: o browser casa um
  `Set-Cookie` de limpeza por `name + path` — se o path não bater, o cookie
  antigo não é removido, fica um cookie "fantasma" esquecido no browser.

- `refreshToken()` (`POST /v1/auth/refresh`) — extrai o cookie de refresh,
  se não existir lança `InvalidRefreshTokenException` (mesmo 401 dos outros
  casos de token inválido), chama `authenticationService.refresh(...)`,
  remonta os dois `Set-Cookie` de resposta (o de refresh reenviado renova o
  `maxAge` no browser mesmo sem mudar o valor).

### 9. `config/JwtAuthenticationFilter.java`
Não mudou de comportamento, só passou a chamar a versão genérica de
`extractTokenFromCookie` passando explicitamente seu próprio
`cookieName` (o do access token). Continua sendo o único ponto que lê o
cookie de access token pra autenticar requisições.

### 10. `config/SecurityConfiguration.java`
`POST /v1/auth/**` é `permitAll()` — já cobre `/v1/auth/refresh` e
`/v1/auth/logout` sem precisar de ajuste extra.

## Decisão tomada: sem rotação de refresh token

Rotação = trocar o refresh token a cada uso (revogar o antigo, emitir um
novo). É mais seguro (limita o dano de um token vazado, permite detectar
reuso indevido), mas mais complexo. Optamos por **não rotacionar** por
enquanto — o mesmo refresh token vale até `expiresAt` (14 dias) ou até ser
revogado manualmente. Fica anotado como possível melhoria futura.

## O fluxo completo, do ponto de vista do cliente

O servidor **não chama o refresh sozinho** — ele não tem como, porque o
access token é stateless (o servidor só descobre que expirou quando tenta
validar numa requisição, não tem como avisar o cliente antes). Quem orquestra
é o **frontend**:

1. Requisição normal falha com `401` (access token expirou).
2. Frontend detecta o 401 e chama `POST /v1/auth/refresh` (o cookie de refresh
   vai junto automaticamente, é `httpOnly`).
3. Se aceito, servidor devolve novo cookie de access token.
4. Frontend repete a requisição original que tinha falhado.
5. Se o refresh também falhar (401), aí sim redireciona pro login.

Isso normalmente é implementado com um **interceptor** de resposta HTTP no
cliente (ex: Axios), com cuidado extra pra não disparar múltiplos refreshes
em paralelo se várias requisições falharem ao mesmo tempo.

## Pendências conhecidas (não implementadas ainda)

- **Logout não revoga no banco.** Hoje só limpa os cookies do navegador — o
  `RefreshToken` correspondente continua `revoked = false` e válido no banco
  até expirar sozinho. Pra invalidar a sessão de verdade no servidor, falta
  no `logout()`: extrair o refresh token do cookie, achar a entidade, marcar
  `revoked = true`, salvar.
- **Rate limiting no `/login`** — não implementado, mencionado como risco
  (força bruta de senha).
- **`TokenProvider.isTokenValid` usa `catch(Exception ex)` genérico** — captura
  qualquer coisa, não só as exceções esperadas de JWT inválido/expirado.
  Ponto de atenção, não corrigido.
- **CORS não configurado** — não há `CorsConfigurationSource`/`.cors(...)` no
  `SecurityConfiguration`. Se o frontend rodar em outra origem (porta/domínio
  diferente do backend) e fizer requisições com cookies, isso vai dar
  problema (preflight `OPTIONS` cai em `anyRequest().authenticated()` → 401).
