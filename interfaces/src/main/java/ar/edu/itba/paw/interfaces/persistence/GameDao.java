package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GameDao {

    public Optional<Game> findById(long id);

    public List<Game> searchByName(String name);

    public List<Game> searchByGenre(Genre genre);

    public List<Game> findAll();

    public Game create(String name, Genre genre);

    Optional<Game> findByName(String name);
}
