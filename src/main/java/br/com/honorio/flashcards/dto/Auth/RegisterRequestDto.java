package br.com.honorio.flashcards.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
  @NotBlank String name,
  @NotBlank String email,
  @NotBlank String password
) {}
