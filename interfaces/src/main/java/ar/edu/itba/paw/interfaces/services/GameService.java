package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Game;
import ar.edu.itba.paw.model.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GameService {

    public Optional<Game> findById(long id);

    Optional<Game> findByName(String name);

    public List<Game> searchByName(String name);

    public List<Game> searchByGenre(Genre genre);

    public List<Game> findAll();

    public Game create(String name, Genre genre);
}
