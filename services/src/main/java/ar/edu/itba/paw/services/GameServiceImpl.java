package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.NameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;

    public GameServiceImpl(final GameDao gameDao){
        this.gameDao = gameDao;
    }

    @Override
    public Optional<Game> findById(long id) {
        return gameDao.findById(id);
    }

    @Override
    public Optional<Game> findByName(String name) {
        return gameDao.findByName(name);
    }

    @Override
    public List<Game> searchByName(String name) {
        return gameDao.searchByName(name);
    }

    @Override
    public List<Game> searchByGenre(Genre genre) {
        return gameDao.searchByGenre(genre);
    }

    @Override
    public List<Game> findAll() {
        return gameDao.findAll();
    }

    @Override
    public Game create(String name, Genre genre, Integer image_id) {
        if(gameDao.checkNameExists(name)){
            throw new NameAlreadyUsedException(name);
        }
        return gameDao.create(name, genre, image_id);
    }

    @Override
    public Game createWithFormats(String name, Genre genre, List<GameFormat> formats, byte[] image) {
        return gameDao.createWithFormats(name, genre, formats, image);
    }

    @Override
    public List<GameFormat> getFormats(Long gameId) {
        return gameDao.getFormats(gameId);
    }

    @Override
    public void addFavourite(Long user_id, Long game_id) {
        gameDao.addFavourite(user_id, game_id);
    }

    @Override
    public List<Game> getFavourites(Long user_id) {
        return gameDao.getFavourites(user_id);
    }

    @Override
    public List<Game> findAllPaged(Long page){
        return gameDao.findAllPaged(page);
    }

    @Override
    public Long getPageAmount(){
        return gameDao.getPageAmount();
    }
}
