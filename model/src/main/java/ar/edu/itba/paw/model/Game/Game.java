package ar.edu.itba.paw.model.Game;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Genre;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_id_seq")
    @SequenceGenerator(sequenceName = "game_id_seq", name = "game_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private  String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", columnDefinition = "genre_enum")
//    @ColumnTransformer(read = "genre::text", write = "?::genre_enum")
    private Genre genre;

    @Column(name = "image_id")
    private Integer image_id;

    @ManyToMany(mappedBy = "favouriteGames", fetch = FetchType.LAZY)
    private List<User> likedByUsers = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GameFormat> formats = new ArrayList<>();

    public Game(){}

    public Game(String name, Genre genre, Integer image_id){
        this.name = name;
        this.genre = genre;
        this.image_id = image_id;
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setImage_id(Integer image_id) {
        this.image_id = image_id;
    }

    public List<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<User> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public List<GameFormat> getFormats() {
        return formats;
    }

    public void setFormats(List<GameFormat> formats) {
        this.formats = formats;
    }
}
