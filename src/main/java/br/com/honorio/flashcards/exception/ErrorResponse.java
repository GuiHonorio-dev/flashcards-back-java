package br.com.honorio.flashcards.exception;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String message) {
}
