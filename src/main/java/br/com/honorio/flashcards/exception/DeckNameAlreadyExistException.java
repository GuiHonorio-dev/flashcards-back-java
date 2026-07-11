package br.com.honorio.flashcards.exception;

public class DeckNameAlreadyExistException extends RuntimeException {
  public DeckNameAlreadyExistException(String name) {
    super("Baralho ja criado com o nome: " + name);
  }
}
