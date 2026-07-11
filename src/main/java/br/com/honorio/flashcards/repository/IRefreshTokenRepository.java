package br.com.honorio.flashcards.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.honorio.flashcards.model.RefreshToken;

public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
