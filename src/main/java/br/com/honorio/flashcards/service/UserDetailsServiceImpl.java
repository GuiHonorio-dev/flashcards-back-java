package br.com.honorio.flashcards.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.honorio.flashcards.repository.IStudentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final IStudentRepository studentRepository;

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return studentRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
  
  }
  
}
