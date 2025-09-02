package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<User> getTournamentParticipants(Long tournament_id) {
		return tournamentDao.getTournamentParticipants(tournament_id);
	}

    @Override
    public void createMatches(Long tournament_id) {
		tournamentDao.createMatches(tournament_id);
	}
    
    @Override
    public Optional<Structure> getTournamentStructure(Long tournament_id){
    	return tournamentDao.getTournamentStructure(tournament_id);
    }
    
    @Override
    public void loadScores(Long match_id, Long tournament_id, Integer local_score, Integer visitor_score) {
    	tournamentDao.loadScores(match_id, tournament_id, local_score, visitor_score);
    }
//   
//    @Override
//    public void joinTournamentTeam(Long team_id, Long tournament_id) {
//        tournamentDao.joinTournamentTeam(team_id, tournament_id);
//    }
//    
    @Override
	public List<Match> getTournamentMatches(Long tournament_id) {
		return tournamentDao.getTournamentMatches(tournament_id);
	}
}
