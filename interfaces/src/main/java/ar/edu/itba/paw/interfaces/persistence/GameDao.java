package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GameDao {

    public Optional<Game> findById(long id);

    public List<Game> searchByName(String name);

    public List<Game> searchByGenre(Genre genre);

    public List<Game> findAll();

    public Game create(String name, Genre genre, Integer image_id);

    public Game createWithFormats(String name, Genre genre, List<GameFormat> formats, byte[] image);

    Optional<Game> findByName(String name);

    public List<GameFormat> getFormats(Long gameId);

    void addFavourite(Long user_id, Long game_id);

    List<Game> getFavourites(Long user_id);

    List<Game> findAllPaged(Long page);

    Long getPageAmount();

    Optional<GameFormat> getFormatById(Long id);
}
