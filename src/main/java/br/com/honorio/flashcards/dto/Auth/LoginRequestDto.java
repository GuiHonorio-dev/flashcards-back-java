package br.com.honorio.flashcards.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
  @NotBlank String email, 
  @NotBlank String password

) {}
