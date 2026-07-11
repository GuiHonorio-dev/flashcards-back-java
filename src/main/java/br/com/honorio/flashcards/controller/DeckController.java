package br.com.honorio.flashcards.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.honorio.flashcards.dto.Deck.CreateDeckRequestDto;
import br.com.honorio.flashcards.dto.Deck.CreateDeckResponseDto;
import br.com.honorio.flashcards.dto.Deck.DeckResponseDto;
import br.com.honorio.flashcards.dto.Deck.UpdateDeckRequestDto;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.service.DeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/decks")
@RequiredArgsConstructor
public class DeckController {
  
  private final DeckService deckService;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public CreateDeckResponseDto create(@Valid @RequestBody CreateDeckRequestDto deckDto, @AuthenticationPrincipal Student student) {
    return deckService.create(deckDto.name(), student.getId());
  }

  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<DeckResponseDto> getDecks(@AuthenticationPrincipal Student student) {
    return deckService.findAllByStudent(student.getId());
  }

  @DeleteMapping("/{deckId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDeck(@AuthenticationPrincipal Student student, @PathVariable String deckId) {
    deckService.deleteDeck(deckId, student.getId());
  }

  @PatchMapping("/{deckId}")
  @ResponseStatus(HttpStatus.OK)
  public void updateDeck(@AuthenticationPrincipal Student student, @PathVariable String deckId, @Valid @RequestBody UpdateDeckRequestDto updateDeckDto) {
    deckService.updateDeck(updateDeckDto, deckId, student.getId());
  }

  @PatchMapping("/{deckId}/review")
  @ResponseStatus(HttpStatus.OK)
  public void setReview(@AuthenticationPrincipal Student student, @PathVariable String deckId) {
    deckService.setReview(deckId, student.getId());
  }


}
