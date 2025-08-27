package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Genre;

public class Game {

    private final long id;
    private final String name;
    private final Genre genre;

    public Game(long id, String name, Genre genre){
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

    public long getId() {
        return id;
    }
}
