package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.NameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.persistence.GameFormatDao;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;
    private final GameFormatDao gameFormatDao;
    private final ImageDao imageDao;

    public GameServiceImpl(final GameDao gameDao, final GameFormatDao gameFormatDao, final ImageDao imageDao){
        this.gameDao = gameDao;
        this.gameFormatDao = gameFormatDao;
        this.imageDao = imageDao;
    }

    @Override
    public Optional<Game> findById(long id) {
        return gameDao.findById(id);
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

    @Transactional
    @Override
    public Game create(String name, Genre genre, Integer image_id) {
        if(gameDao.checkNameExists(name)){
            throw new NameAlreadyUsedException(name);
        }
        return gameDao.create(name, genre, image_id);
    }

    @Transactional
    @Override
    public Game createWithFormats(String name, Genre genre, List<GameFormat> formats, byte[] image) {
        Long imageId = imageDao.insertImage(image);
        for (GameFormat f : formats){
            gameFormatDao.insertFormat(f);
        }
        return gameDao.create(name, genre, imageId.intValue());
    }

    @Override
    public List<GameFormat> getFormats(Long gameId) {
        return gameFormatDao.getFormats(gameId);
    }

    @Transactional
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

    @Override
    public Optional<GameFormat> getFormatById(Long id) {
        if (id == null){
            return Optional.empty();
        }
        return gameFormatDao.getFormatById(id);
    }
}
