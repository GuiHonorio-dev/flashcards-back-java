package br.com.honorio.flashcards.service;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class CookieService {
   
  @Value("${jwt.cookie-name}")
  private String cookieName;
  @Value("${jwt.cookie-secure}")
  private boolean cookieSecure;

  public ResponseCookie buildCookie(String cookieName, String token, Duration maxAge, String path) {
    return ResponseCookie.from(cookieName, token)
    .httpOnly(true)
    .secure(cookieSecure)
    .maxAge(maxAge)
    .sameSite("Strict")
    .path(path)
    .build();
  }

  public String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();

    if(cookies == null) {
      return null;
    }

    return Arrays.stream(cookies).filter(cookie -> cookieName.equals(cookie.getName()))
    .map(Cookie::getValue)
    .findFirst()
    .orElse(null);
  }
}
