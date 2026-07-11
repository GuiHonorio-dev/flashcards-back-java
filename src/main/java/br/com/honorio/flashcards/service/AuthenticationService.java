package br.com.honorio.flashcards.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.honorio.flashcards.config.TokenProvider;
import br.com.honorio.flashcards.dto.Auth.LoginRequestDto;
import br.com.honorio.flashcards.dto.Auth.RegisterRequestDto;
import br.com.honorio.flashcards.dto.Auth.TokenPairDto;
import br.com.honorio.flashcards.exception.EmailAlreadyExistsException;
import br.com.honorio.flashcards.exception.InvalidRefreshTokenException;
import br.com.honorio.flashcards.model.RefreshToken;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.repository.IRefreshTokenRepository;
import br.com.honorio.flashcards.repository.IStudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class AuthenticationService {
  
  private final AuthenticationManager authenticationManager;
  private final IStudentRepository studentRepository;
  private final IRefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Value("${jwt.refresh-expiration}")
  private long refreshExpirationTime;

  public void register(@Valid @RequestBody RegisterRequestDto registerDto) {
    Student student = studentRepository.findByEmail(registerDto.email()).orElse(null);
  
    if(student != null) {
      throw new EmailAlreadyExistsException(registerDto.email());
    }

    student = Student.builder()
    .name(registerDto.name())
    .email(registerDto.email())
    .password(passwordEncoder.encode(registerDto.password()))
    .build();

    studentRepository.save(student);
  }

  public TokenPairDto login(@Valid @RequestBody LoginRequestDto loginDto) {
    try {
      Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
      String token = tokenProvider.gerarToken(auth);

      String refreshToken = tokenProvider.generateRefreshToken();
      String hash = tokenProvider.hashToken(refreshToken);

      Student student = (Student) auth.getPrincipal();
      RefreshToken hashRefreshToken = RefreshToken.builder()
      .student(student)      
      .expiresAt(Instant.now().plusMillis(refreshExpirationTime))
      .tokenHash(hash)
      .build();

      refreshTokenRepository.save(hashRefreshToken);

      return new TokenPairDto(token, refreshToken);
    } catch(BadCredentialsException ex) {
      throw new BadCredentialsException("Credenciais invalidas");
    } catch(Exception ex) {
      throw ex;
    }
  }

  public TokenPairDto refresh(String rawRefreshToken) {
    String hash = tokenProvider.hashToken(rawRefreshToken);
    RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash).orElseThrow(() -> new InvalidRefreshTokenException("Refresh token invalido"));
    if(stored.getExpiresAt().isBefore(Instant.now())) {
      throw new InvalidRefreshTokenException("Refresh token expirado");
    }

    String newAccessToken = tokenProvider.buildToken(stored.getStudent());
    
    return new TokenPairDto(newAccessToken, rawRefreshToken);

  }

}

