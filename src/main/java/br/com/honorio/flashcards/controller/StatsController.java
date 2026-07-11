package br.com.honorio.flashcards.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.honorio.flashcards.dto.Stats.SetReviewRequestDto;
import br.com.honorio.flashcards.dto.Stats.TotalReviewsDto;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.service.CardReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/v1/stats")
@RequiredArgsConstructor
public class StatsController {
  
  private final CardReviewService cardReviewService;

  @GetMapping("/cards/review")
  public TotalReviewsDto getTotalReviewsByStudent(@AuthenticationPrincipal Student student) {
    long total = cardReviewService.countByStudentId(student.getId());
    return new TotalReviewsDto(total);
  }

  @PostMapping("/cards/review")
  @ResponseStatus(HttpStatus.CREATED)
  public void setReview(@Valid @RequestBody SetReviewRequestDto reviewDto, @AuthenticationPrincipal Student student) {
    cardReviewService.setReview(reviewDto, student.getId());
  }
}
