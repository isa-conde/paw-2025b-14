package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.Tournament.TournamentImg;
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
    public Map<Integer, Map<Integer, List<MatchWithPlayers>>> getTournamentMatchesByGroup(Long tournament_id) {
        List<MatchWithPlayers> matches = tournamentDao.getTournamentMatches(tournament_id);
        Map<Integer, Map<Integer, List<MatchWithPlayers>>> tournamentMatchesByGroup = new TreeMap<>();
        Tournament t = findById(tournament_id).orElse(null);

        if (!matches.isEmpty() && t != null) {
            for (MatchWithPlayers match : matches) {
                if (match.getGroupNumber() != null && match.getStage() != null) {
                    int group = match.getGroupNumber();
                    int stage = match.getStage();
                    tournamentMatchesByGroup.putIfAbsent(group, new TreeMap<>());
                    Map<Integer, List<MatchWithPlayers>> matchesByStage = tournamentMatchesByGroup.get(group);
                    matchesByStage.putIfAbsent(stage, new ArrayList<>());
                    matchesByStage.get(stage).add(match);
                }
            }
        }
        return tournamentMatchesByGroup;
    }


	@Override
    public List<TournamentImg> findWithImg(TournamentFilter tournamentFilter){
        return tournamentDao.findWithImg(tournamentFilter);
    }

    @Override
    public Map<Integer, List<ParticipantUserInfo>> getTournamentParticipantsByGroup(Long tournament_id) {
        List<User> users = tournamentDao.getTournamentUsers(tournament_id);
        List<ParticipantUser> participants = tournamentDao.getTournamentParticipantUsers(tournament_id);
        List<MatchWithPlayers> matches = tournamentDao.getTournamentMatches(tournament_id);

        Map<Long, Integer> userPoints = new HashMap<>();
        for (ParticipantUser p : participants) {
            userPoints.put(p.getUser_id(), p.getPoints());
        }

        Map<Long, User> usersById = new HashMap<>();
        for (User u : users) {
            usersById.put(u.getId(), u);
        }

        Map<Integer, Set<Long>> groupUserIds = new HashMap<>();
        for (MatchWithPlayers match : matches) {
            if (match.getGroupNumber() != null) {
                if (match.getLocalId() != null) {
                    groupUserIds.computeIfAbsent(match.getGroupNumber(), g -> new HashSet<>())
                            .add(match.getLocalId());
                }
                if (match.getVisitorId() != null) {
                    groupUserIds.computeIfAbsent(match.getGroupNumber(), g -> new HashSet<>())
                            .add(match.getVisitorId());
                }
            }
        }

        Map<Integer, List<ParticipantUserInfo>> participantsByGroup = new TreeMap<>();
        for (Map.Entry<Integer, Set<Long>> entry : groupUserIds.entrySet()) {
            int groupNumber = entry.getKey();
            List<ParticipantUserInfo> groupList = new ArrayList<>();

            for (Long userId : entry.getValue()) {
                User user = usersById.get(userId);
                if (user != null) {
                    int points = userPoints.getOrDefault(userId, 0);
                    groupList.add(new ParticipantUserInfo(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            points
                    ));
                }
            }
            groupList.sort((p1, p2) -> p2.getPoints().compareTo(p1.getPoints()));
            participantsByGroup.put(groupNumber, groupList);
        }

        return participantsByGroup;
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
    public List<TournamentImg> findUserActiveTournaments(Long userId) {
        return tournamentDao.findUserActiveTournaments(userId);
    }

    @Override
    public List<TournamentImg> findUserPastTournaments(Long userId) {
        return tournamentDao.findUserPastTournaments(userId);
    }

    @Override
    public List<TournamentImg> searchByName(String name){
        return tournamentDao.searchByName(name);
    }
}
