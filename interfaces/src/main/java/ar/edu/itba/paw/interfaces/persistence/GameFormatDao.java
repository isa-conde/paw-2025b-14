package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Game.GameFormat;

import java.util.List;
import java.util.Optional;

public interface GameFormatDao {

    void insertFormat(GameFormat gameFormat);

    List<GameFormat> getFormats(long gameId);

    Optional<GameFormat> findById(long id);

    int getPlayersPerTeam(long id);

}
