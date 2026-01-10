package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TokenDao {

    Token create(long userId, long token, LocalDate expiryDate);

    Optional<Token> findByToken(long token);

    void markAsUsed(long tokenId);

    void deleteExpiredTokens();

    List<Token> findAssignedTokens(User user);

}
