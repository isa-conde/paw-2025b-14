package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameForm {

    @Size(min = 6, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    private String name;

    private Genre genre;

    private List<GameFormat> formats = new ArrayList<>(Arrays.asList(
            new GameFormat(null, null, null),
            new GameFormat(null, null, null),
            new GameFormat(null, null, null)
    ));

    private MultipartFile image;

    public GameForm(){}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Genre getGenre() {
        return genre;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public List<GameFormat> getFormats() {
        return formats;
    }
    public void setFormats(List<GameFormat> formats) {
        this.formats = formats;
    }

    public MultipartFile getImage() {
        return image;
    }
    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
