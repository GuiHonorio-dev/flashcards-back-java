package br.com.honorio.flashcards.dto.Student;

import jakarta.validation.constraints.NotBlank;

public record CreateStudentRequest(
    @NotBlank String name,
    @NotBlank String email,
    @NotBlank String password) {}
