package br.com.honorio.flashcards.exception;

public class EmailAlreadyExistsException extends RuntimeException {

  public EmailAlreadyExistsException(String email) {
    super("Já existe um aluno cadastrado com o e-mail: " + email);
  }
}
