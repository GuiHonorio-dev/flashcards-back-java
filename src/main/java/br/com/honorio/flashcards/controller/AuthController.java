package br.com.honorio.flashcards.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.honorio.flashcards.dto.Auth.LoginRequestDto;
import br.com.honorio.flashcards.dto.Auth.RegisterRequestDto;
import br.com.honorio.flashcards.dto.Auth.TokenPairDto;
import br.com.honorio.flashcards.exception.InvalidRefreshTokenException;
import br.com.honorio.flashcards.service.AuthenticationService;
import br.com.honorio.flashcards.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  @Value("${jwt.expiration}")
  private long expirationTime;

  @Value("${jwt.cookie-name}")
  private String cookieName;


  @Value("${jwt.refresh-cookie-name}")
  private String refreshCookieName;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpirationTime;


  private final AuthenticationService authenticationService;
  private final CookieService cookieService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@Valid @RequestBody RegisterRequestDto registerDto) {
    authenticationService.register(registerDto);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public void login(@Valid @RequestBody LoginRequestDto loginDto, HttpServletResponse response) {
    TokenPairDto tokens = authenticationService.login(loginDto);
    
    ResponseCookie access = cookieService.buildCookie(cookieName,tokens.accessToken(), Duration.ofMillis(expirationTime), "/");
    response.addHeader(HttpHeaders.SET_COOKIE, access.toString());

    ResponseCookie refresh = cookieService.buildCookie(refreshCookieName,tokens.refreshToken(), Duration.ofMillis(refreshExpirationTime), "/v1/auth");
    response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  public void logout(HttpServletResponse response) {
    ResponseCookie access = cookieService.buildCookie(cookieName, "", Duration.ZERO, "/");
    response.addHeader(HttpHeaders.SET_COOKIE, access.toString());

    ResponseCookie refresh = cookieService.buildCookie(refreshCookieName, "", Duration.ZERO, "/v1/auth");
    response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

  }

  @PostMapping("/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String rawRefreshToken = cookieService.extractTokenFromCookie(request, refreshCookieName);
    if(rawRefreshToken == null) {
      throw new InvalidRefreshTokenException("Refresh token nao encontrado");
    }

    TokenPairDto tokens = authenticationService.refresh(rawRefreshToken);

    ResponseCookie access = cookieService.buildCookie(cookieName, tokens.accessToken(), Duration.ofMillis(expirationTime), "/");
    response.addHeader(HttpHeaders.SET_COOKIE, access.toString());

    ResponseCookie refresh = cookieService.buildCookie(refreshCookieName, tokens.refreshToken(), Duration.ofMillis(refreshExpirationTime), "/v1/auth");
    response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
  }

}
