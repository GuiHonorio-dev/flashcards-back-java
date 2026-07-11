package br.com.honorio.flashcards.service;

import org.springframework.stereotype.Service;

import br.com.honorio.flashcards.dto.Student.CreateStudentRequest;
import br.com.honorio.flashcards.dto.Student.CreateStudentResponse;
import br.com.honorio.flashcards.exception.EmailAlreadyExistsException;
import br.com.honorio.flashcards.model.Student;
import br.com.honorio.flashcards.repository.IStudentRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class StudentService {

  private final IStudentRepository studentRepository;

  public CreateStudentResponse create(CreateStudentRequest studentDto) {

    Student student = studentRepository.findByEmail(studentDto.email()).orElse(null);

    if(student != null) {
      throw new EmailAlreadyExistsException(studentDto.email());
    } 

    student = Student.builder()
    .name(studentDto.name())
    .email(studentDto.email())
    .password(studentDto.password()) // Implementar hash
    .build();

    Student savedStudent = studentRepository.save(student);

    return new CreateStudentResponse(savedStudent.getId());  
  }
}
