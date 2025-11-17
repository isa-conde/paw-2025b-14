package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GameService {

    Optional<Game> findById(long id);

    List<Game> searchByName(String name, Long page);

    int countSearchByNameGame(String name);

    List<Game> findAll();

    Game create(String name, Genre genre, Integer imageId);

    List<GameFormat> getFormats(Long gameId);

    List<Game> getFavourites(Long userId);

    List<Game> findAllPaged(Long page);

    Long getPageAmount();
}
