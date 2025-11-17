package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Game.GameFormat;

import java.util.List;
import java.util.Optional;

public interface GameFormatDao {

    List<GameFormat> getFormats(Long gameId);

    Optional<GameFormat> findById(Long id);
}
