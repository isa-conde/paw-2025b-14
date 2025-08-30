package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Genre;

public class Game {

    private final Long id;
    private final String name;
    private final Genre genre;

    public Game(Long id, String name, Genre genre){
        this.id = id;
        this.name = name;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public Genre getGenre() {
        return genre;
    }

    public Long getId() {
        return id;
    }
}
