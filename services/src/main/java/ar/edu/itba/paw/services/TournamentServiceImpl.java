package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
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
    public Map<Integer, Map<Integer, List<MatchWithPlayers>>> getTournamentMatchesByGroup(Long tournamentId) {
        List<MatchWithPlayers> matches = tournamentDao.getTournamentMatches(tournamentId);
        Tournament t = findById(tournamentId).orElse(null);
        Map<Integer, Map<Integer, List<MatchWithPlayers>>> result = new TreeMap<>();
        if (t == null || matches.isEmpty()) return result;

        Map<Long, Integer> userGroup = tournamentDao.getTournamentGroupsByUser(tournamentId);

        for (MatchWithPlayers m : matches) {
            Integer stage = m.getStage();
            if (stage == null) continue;

            Integer gLocal   = (m.getLocalId()   != null) ? userGroup.get(m.getLocalId())   : null;
            Integer gVisitor = (m.getVisitorId() != null) ? userGroup.get(m.getVisitorId()) : null;

            Integer group = null;
            if (gLocal != null && gVisitor != null) {
                if (!gLocal.equals(gVisitor)) continue;
                group = gLocal;
            } else if (gLocal != null) {
                group = gLocal;
            } else if (gVisitor != null) {
                group = gVisitor;
            } else {
                continue;
            }

            result.computeIfAbsent(group, g -> new TreeMap<>())
                    .computeIfAbsent(stage, s -> new ArrayList<>())
                    .add(m);
        }
        return result;
    }

    @Override
    public List<ParticipantUserInfo> getTournamentParticipants(Long tournamentId) {
        List<User> users = tournamentDao.getTournamentUsers(tournamentId);
        Map<Long, User> usersById = new HashMap<>();
        for (User u : users) usersById.put(u.getId(), u);

        List<ParticipantUser> participants = tournamentDao.getTournamentParticipantUsers(tournamentId);

        List<ParticipantUserInfo> result = new ArrayList<>(participants.size());
        for (ParticipantUser p : participants) {
            User u = usersById.get(p.getUser_id());
            if (u == null) continue;

            Integer groupNumber = p.getGroupNumber();
            result.add(new ParticipantUserInfo(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    p.getPoints(),
                    groupNumber
            ));
        }

        result.sort((a, b) -> {
            Integer ga = a.getGroupNumber(), gb = b.getGroupNumber();
            if (ga == null && gb != null) return 1;
            if (ga != null && gb == null) return -1;
            if (ga != null && gb != null) {
                int cmp = Integer.compare(ga, gb);
                if (cmp != 0) return cmp;
            }
            int ptsCmp = b.getPoints().compareTo(a.getPoints());
            if (ptsCmp != 0) return ptsCmp;
            return a.getUsername().compareToIgnoreCase(b.getUsername());
        });

        return result;
    }


    @Override
    public List<Tournament> findByCreator(Long creator_id) {
        return tournamentDao.findByCreator(creator_id);
    }

    @Override
    public void setFinished(Long tournament_id, Long match_id) {
        tournamentDao.setFinished(tournament_id, match_id);
    }

    @Override
    public void closeInscriptions(Long tournament_id){
        tournamentDao.closeInscriptions(tournament_id);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        return tournamentDao.hasJoined(userId, tournamentId);
    }

    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        tournamentDao.setMatchWinner(matchId, tournamentId, winner);
    }

    @Override
    public List<Tournament> findUserActiveTournaments(Long userId) {
        return tournamentDao.findUserActiveTournaments(userId);
    }

    @Override
    public List<Tournament> findUserPastTournaments(Long userId) {
        return tournamentDao.findUserPastTournaments(userId);
    }

    @Override
    public List<Tournament> searchByName(String name){
        return tournamentDao.searchByName(name);
    }

    @Override
    public void startTournament(Long tournament_id){
        tournamentDao.startTournament(tournament_id);
    }
}
