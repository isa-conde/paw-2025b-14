package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Genre;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class GameForm {

    @Size(min = 6, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    private String name;

    private Genre genre;

    public GameForm(){}

    public String getName() {
        return name;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
