package br.com.honorio.flashcards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.honorio.flashcards.model.Student;

public interface IStudentRepository extends JpaRepository<Student, String> {
  Optional<Student> findByEmail(String email);
}
