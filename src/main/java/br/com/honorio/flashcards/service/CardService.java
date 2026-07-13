package br.com.honorio.flashcards.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.honorio.flashcards.dto.Card.CreateCardRequestDto;
import br.com.honorio.flashcards.dto.Card.CreateCardResponseDto;
import br.com.honorio.flashcards.dto.Card.GetCardResponseDto;
import br.com.honorio.flashcards.dto.Card.UpdateCardRequestDto;
import br.com.honorio.flashcards.exception.NotFoundException;
import br.com.honorio.flashcards.model.Card;
import br.com.honorio.flashcards.model.Deck;
import br.com.honorio.flashcards.repository.ICardRepository;
import br.com.honorio.flashcards.repository.IDeckRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {
  
  private final IDeckRepository deckRepository;
  private final ICardRepository cardRepository;

  public CreateCardResponseDto create(CreateCardRequestDto cardDto, String studentId) {
    Deck deck = deckRepository.findByIdAndStudentId(cardDto.deckId(), studentId).orElseThrow(() -> new NotFoundException("Deck não encontrado"));
    
    Card card = Card.builder()
    .deck(deck)
    .question(cardDto.question())
    .answer(cardDto.answer())
    .build();

    Card savedCard = cardRepository.save(card);

    return new CreateCardResponseDto(savedCard.getId());

  }

  public List<GetCardResponseDto> getAllByDeckIdAndStudentId(String deckId, String studentId){
    Deck deck = deckRepository.findByIdAndStudentId(deckId, studentId).orElse(null);

    if(deck == null) {
      throw new NotFoundException("Deck nao encontrado");
    } 
    
    List<GetCardResponseDto> cards = deck.getCards()
    .stream()
    .map(card -> new GetCardResponseDto(card.getId(), card.getQuestion(), card.getAnswer(), card.getCreatedAt()))
    .toList();

    return cards;

  }

  public List<GetCardResponseDto> getDueCards(String studentId) {
    return cardRepository.findDueCards(studentId, Instant.now())
    .stream()
    .map(card -> new GetCardResponseDto(card.getId(), card.getQuestion(), card.getAnswer(), card.getCreatedAt()))
    .toList();
  }

  public void deleteCard(String cardId, String studentID) {
    Card card = cardRepository.findByIdAndStudentId(cardId, studentID).orElseThrow(() -> new NotFoundException("Carta nao encontrada"));

    cardRepository.delete(card);
  }

  public void updateCard(UpdateCardRequestDto updateDto, String cardId, String studentId) {
    Card card = cardRepository.findByIdAndStudentId(cardId, studentId).orElseThrow(() -> new NotFoundException("Carta nao encontrada"));

    if (updateDto.question() != null && !updateDto.question().isBlank()) {
      card.setQuestion(updateDto.question());
    }

    if (updateDto.answer() != null && !updateDto.answer().isBlank()) {
      card.setAnswer(updateDto.answer());
    }

    if (updateDto.deckId() != null && !updateDto.deckId().isBlank()) {
      Deck deck = deckRepository.findByIdAndStudentId(updateDto.deckId(), studentId)
          .orElseThrow(() -> new NotFoundException("Deck não encontrado"));
      card.setDeck(deck);
    }

    cardRepository.save(card);
  }
}
