package ar.edu.itba.paw.model.Game;

import ar.edu.itba.paw.model.enums.Genre;

public class Game {

    private final Long id;
    private final String name;
    private final Genre genre;
    private final Integer image_id;

    public Game(Long id, String name, Genre genre, Integer imageId){
        this.id = id;
        this.name = name;
        this.genre = genre;
        image_id = imageId;
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

    public Integer getImage_id() {
        return image_id;
    }
}
