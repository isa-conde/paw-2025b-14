package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Token;

import java.time.LocalDate;
import java.util.Optional;

public interface TokenDao {

    Token create(Long userId, Long token, LocalDate expiryDate);

    Optional<Token> findByToken(Long token);

    void markAsUsed(Long tokenId);

    void deleteExpiredTokens();

}
