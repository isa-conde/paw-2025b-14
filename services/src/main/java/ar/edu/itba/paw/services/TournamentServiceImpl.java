package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Pair;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.TournamentImg;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.*;

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
    public List<Tournament> findGameTournaments(Long game_id){
        return tournamentDao.findGameTournaments(game_id);
    }


    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image, Boolean openInscriptions, Boolean isFinished) {
        return tournamentDao.create(creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image, openInscriptions, isFinished);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        tournamentDao.joinTournamentUser(user_id, tournament_id);
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

	@Override
	public List<MatchWithPlayers> getTournamentMatchesWithPlayers(Long tournament_id) {
		return tournamentDao.getTournamentMatchesWithPlayers(tournament_id);
	}

	@Override
    public List<TournamentImg> findWithImg(TournamentFilter tournamentFilter){
        return tournamentDao.findWithImg(tournamentFilter);
    }


    @Override
    public List<ParticipantUserInfo> getTournamentParticipants(Long tournament_id) {
        List<User> users = tournamentDao.getTournamentUsers(tournament_id);
        List<ParticipantUser> participants = tournamentDao.getTournamentParticipantUsers(tournament_id);

        Map<Long, Integer> userPoints = new HashMap<>();
        for (ParticipantUser p : participants) {
            userPoints.put(p.getUser_id(), p.getPoints());
        }

        List<ParticipantUserInfo> result = new ArrayList<>();
        for (User user : users) {
            int points = userPoints.getOrDefault(user.getId(), 0);
            result.add(new ParticipantUserInfo(user.getId(), user.getUsername(), user.getEmail(), points));
        }

        result.sort((p1, p2) -> p2.getPoints().compareTo(p1.getPoints()));

        return result;
    }

    @Override
    public Optional<TournamentImg> findByIdWithImg(Long id){
        return tournamentDao.findByIdWithImg(id);
    }
    @Override
    public List<TournamentImg> findByCreatorImg(Long creator_id) {
        return tournamentDao.findByCreatorImg(creator_id);
    }

    @Override
    public void setFinished(Long tournament_id) {
        tournamentDao.setFinished(tournament_id);
    }

    @Override
    public void closeInscriptions(Long tournament_id){
        tournamentDao.closeInscriptions(tournament_id);
    }

    @Override
    public List<Pair<String,String>> getGenericMatches(Long tournament_id) {
		return tournamentDao.getGenericMatches(tournament_id);
	}

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        return tournamentDao.hasJoined(userId, tournamentId);
    }

    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        tournamentDao.setMatchWinner(matchId, tournamentId, winner);
    }
}
