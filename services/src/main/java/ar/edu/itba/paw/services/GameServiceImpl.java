package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.GameNotFoundException;
import ar.edu.itba.paw.interfaces.exception.ImageNotFoundException;
import ar.edu.itba.paw.interfaces.exception.NameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.persistence.GameFormatDao;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.enums.Genre;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;
    private final GameFormatDao gameFormatDao;

    public GameServiceImpl(final GameDao gameDao, final GameFormatDao gameFormatDao){
        this.gameDao = gameDao;
        this.gameFormatDao = gameFormatDao;
    }

    @Override
    public Optional<Game> findById(long id) {
        return gameDao.findById(id);
    }

    @Override
    public List<Game> searchByName(String name, int page) {
        return gameDao.searchByName(name, page);
    }

    @Override
    public int countSearchByNameGame(String name) {
        return gameDao.countSearchByNameGame(name);
    }

    @Override
    public List<Game> findAll() {
        return gameDao.findAll();
    }

    @Transactional
    @Override
    public Game create(String name, Genre genre, Integer imageId) {
        if(gameDao.checkNameExists(name)){
            throw new NameAlreadyUsedException(name);
        }
        if(imageId == null) {
            throw new ImageNotFoundException();
        }
        return gameDao.create(name, genre, imageId);
    }

    @Override
    public List<GameFormat> getFormats(Long gameId) {
        if(gameId == null) {
            throw new GameNotFoundException();
        }
        return gameFormatDao.getFormats(gameId);
    }

    @Transactional
    @Override
    public List<Game> getFavourites(long userId) {
        return gameDao.getFavourites(userId);
    }

    @Override
    public List<Game> findAllPaged(long page){
        return gameDao.findAllPaged(page);
    }

    @Override
    public int getPageAmount(){
        return gameDao.getPageAmount();
    }

    @Override
    public GameFormat getFormat(Long formatId){
        return gameFormatDao.findById(formatId);
    }
}
