package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GameDao {

    Optional<Game> findById(long id);

    List<Game> searchByName(String name);

    List<Game> searchByGenre(Genre genre);

    List<Game> findAll();

    Game create(String name, Genre genre, int imageId);

    boolean checkNameExists(String name);

    List<Game> getFavourites(long userId);

    List<Game> findAllPaged(long page);

    long getPageAmount();
}
