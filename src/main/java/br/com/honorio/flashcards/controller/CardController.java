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

import br.com.honorio.flashcards.dto.Card.CreateCardRequestDto;
import br.com.honorio.flashcards.dto.Card.CreateCardResponseDto;
import br.com.honorio.flashcards.dto.Card.GetCardResponseDto;
import br.com.honorio.flashcards.dto.Card.UpdateCardRequestDto;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
public class CardController {
  
  private final CardService cardService;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public CreateCardResponseDto create(@Valid @RequestBody CreateCardRequestDto cardDto, @AuthenticationPrincipal Student student) {
    return cardService.create(cardDto, student.getId());
  }

  @GetMapping("/{deckId}")
  public List<GetCardResponseDto> findAllByDeckId(@Valid @PathVariable String deckId, @AuthenticationPrincipal Student student) {
    return cardService.getAllByDeckIdAndStudentId(deckId, student.getId());
  }

  @DeleteMapping("/{cardId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCard(@AuthenticationPrincipal Student student, @PathVariable String cardId) {
    cardService.deleteCard(cardId, student.getId());
  }

  @PatchMapping("/{cardId}")
  @ResponseStatus(HttpStatus.OK)
  public void updateCard(@AuthenticationPrincipal Student student, @PathVariable String cardId, @Valid @RequestBody UpdateCardRequestDto cardUpdateDto) {
    cardService.updateCard(cardUpdateDto, cardId, student.getId());
  }
}
