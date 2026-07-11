package br.com.honorio.flashcards.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.honorio.flashcards.dto.Student.CreateStudentRequest;
import br.com.honorio.flashcards.dto.Student.CreateStudentResponse;
import br.com.honorio.flashcards.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/students")
@RequiredArgsConstructor
public class StudentController {
  
  private final StudentService studentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CreateStudentResponse create(@Valid @RequestBody CreateStudentRequest studentDto) {
    return studentService.create(studentDto);
  }

}
