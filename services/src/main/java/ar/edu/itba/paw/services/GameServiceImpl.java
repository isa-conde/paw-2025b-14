package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.services.GameService;
import ar.edu.itba.paw.model.Game;
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
    public Game create(String name, Genre genre) {
        return gameDao.create(name, genre);
    }
}
