package br.com.honorio.flashcards.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.honorio.flashcards.model.Student;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenProvider {
  
  @Value("${jwt.expiration}")
  private long expirationTime;

  @Value("${jwt.key}")
  private String key;

  public String gerarToken(Authentication authentication) {
    Student user = (Student) authentication.getPrincipal();
    return buildToken(user);
  }

  public String buildToken(Student student) {
    Date now = new Date();
    Date experidationDate = new Date(now.getTime() + expirationTime);


    return Jwts.builder()
    .subject(student.getUsername())
    .claim("id", student.getId())
    .issuedAt(now)
    .expiration(experidationDate)
    .signWith(getSigningKey())
    .compact();
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(key.getBytes());
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
  }

  public Boolean isTokenValid(String token) {
    try {
      getClaims(token);
      return true;
    } catch(Exception ex) {
      return false;
    }
  }

  public String getUsername(String token) {
    return getClaims(token).getSubject();
  }

  public String getStudentId(String token) {
    return getClaims(token).get("id", String.class);
  }

  public String generateRefreshToken() {
    return UUID.randomUUID().toString();
  }

  public String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(token.getBytes());

      String hashString = Base64.getEncoder().encodeToString(hashBytes);
      
      return hashString;
    } catch(NoSuchAlgorithmException ex) {
      throw new RuntimeException("Erro ao definir refresh token");
    }
  }

 

}
