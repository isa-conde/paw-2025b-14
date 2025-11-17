package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.GameFormatNotFoundException;
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
    public List<Game> searchByName(String name, Long page) {
        return gameDao.searchByName(name, page);
    }

    @Override
    public int countSearchByNameGame(String name) {
        return gameDao.countSearchByNameGame(name);
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
    public Game create(String name, Genre genre, Integer imageId) {
        if(gameDao.checkNameExists(name)){
            throw new NameAlreadyUsedException(name);
        }
        return gameDao.create(name, genre, imageId);
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
    public List<Game> getFavourites(Long userId) {
        return gameDao.getFavourites(userId);
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
    public GameFormat findFormatById(Long id) {
        return gameFormatDao.findById(id).orElseThrow(GameFormatNotFoundException::new);
    }
}
