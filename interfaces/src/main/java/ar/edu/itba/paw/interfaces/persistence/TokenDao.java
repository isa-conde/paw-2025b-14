package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Token;

import java.time.LocalDate;
import java.util.Optional;

public interface TokenDao {

    Token create(long userId, long token, LocalDate expiryDate);

    Optional<Token> findByToken(long token);

    void markAsUsed(long tokenId);

    void deleteExpiredTokens();

}
