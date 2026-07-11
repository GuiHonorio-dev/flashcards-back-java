package br.com.honorio.flashcards.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.honorio.flashcards.dto.Deck.CreateDeckResponseDto;
import br.com.honorio.flashcards.dto.Deck.DeckResponseDto;
import br.com.honorio.flashcards.dto.Deck.UpdateDeckRequestDto;
import br.com.honorio.flashcards.exception.DeckNameAlreadyExistException;
import br.com.honorio.flashcards.exception.NotFoundException;
import br.com.honorio.flashcards.model.Deck;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.repository.IDeckRepository;
import br.com.honorio.flashcards.repository.IStudentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {
  
  private final IDeckRepository deckRepository;
  private final IStudentRepository studentRepository;

  public CreateDeckResponseDto create(String name, String id) {
    Deck deck = deckRepository.findByName(name).orElse(null);

    Student student = studentRepository.findById(id).orElseThrow(() -> new NotFoundException("Estudante não encontrado"));

    if(deck != null) {
      throw new DeckNameAlreadyExistException(name);
    }

    deck = Deck.builder()
    .name(name)
    .student(student)
    .build();

    Deck savedDeck = deckRepository.save(deck);

    return new CreateDeckResponseDto(savedDeck.getId());
  }

  public List<DeckResponseDto> findAllByStudent(String studentId) {
    return deckRepository.findByStudentId(studentId)
      .stream()
      .map(deck -> new DeckResponseDto(deck.getId(), deck.getName(),deck.getReviewedAt(), deck.getCreatedAt()))
      .toList();
  }

  public void deleteDeck(String deckId, String studentId) {
    Deck deck = deckRepository.findByIdAndStudentId(deckId, studentId).orElseThrow(() -> new NotFoundException("Deck nao encontrado"));

    deckRepository.delete(deck);
  }

  public void updateDeck(UpdateDeckRequestDto updateDeckDto,String deckId, String studentId) {
    Deck deck = deckRepository.findByIdAndStudentId(deckId, studentId).orElseThrow(() -> new NotFoundException("Deck nao encontrado"));
    deck.setName(updateDeckDto.name());
    
    deckRepository.save(deck);

  }

  public void setReview(String deckId, String studentId) {
    Deck deck = deckRepository.findByIdAndStudentId(deckId, studentId).orElseThrow(() -> new NotFoundException("Deck nao encontrado"));
    
    deck.setReviewedAt(Instant.now());
    deckRepository.save(deck);
  }
}
