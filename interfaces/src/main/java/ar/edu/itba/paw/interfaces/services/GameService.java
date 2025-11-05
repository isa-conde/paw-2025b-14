package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GameService {

    Optional<Game> findById(long id);

    List<Game> searchByName(String name);

    List<Game> searchByGenre(Genre genre);

    List<Game> findAll();

    Game create(String name, Genre genre, Integer imageId);

    Game createWithFormats(String name, Genre genre, List<GameFormat> formats, byte[] image);

    List<GameFormat> getFormats(Long gameId);

    List<Game> getFavourites(Long userId);

    List<Game> findAllPaged(Long page);

    Long getPageAmount();

    Optional<GameFormat> getFormatById(Long id);
}
