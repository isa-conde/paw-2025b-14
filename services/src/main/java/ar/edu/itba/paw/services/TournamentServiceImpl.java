package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentDao tournamentDao;

    public TournamentServiceImpl(TournamentDao tournamentDao) {
        this.tournamentDao = tournamentDao;
    }


    @Override
    public Optional<Tournament> findById(Long id) {
        if (id != null){
            return tournamentDao.findById(id);
        }
        return Optional.empty();
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter tournamentFilter) {
        return tournamentDao.findTournaments(tournamentFilter);
    }

    @Override
    public Tournament create(Long creatorid, String name, Long gameid, Region region, Elo elo, LocalDate startdate, LocalDate enddate, String format, Structure structure, Integer max_participants) {
        return tournamentDao.create(creatorid, name, gameid, region, elo, startdate, enddate, format, structure, max_participants);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        tournamentDao.joinTournamentUser(user_id, tournament_id);
    }

    @Override
    public void joinTournamentTeam(Long team_id, Long tournament_id) {
        tournamentDao.joinTournamentTeam(team_id, tournament_id);
    }
}
