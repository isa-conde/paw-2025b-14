package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Game.Game;
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

import javax.mail.Multipart;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentDao tournamentDao;
    private final ImageDao imageDao;
    private final ParticipantDao participantDao;
    private final GameDao gameDao;

    public TournamentServiceImpl(TournamentDao tournamentDao, ImageDao imageDao, GameDao gameDao, ParticipantDao participantDao) {
        this.tournamentDao = tournamentDao;
        this.gameDao = gameDao;
        this.imageDao = imageDao;
        this.participantDao = participantDao;
    }

    @Override
    public Optional<Tournament> findById(Long id) {
        if (id != null){
            return tournamentDao.findById(id);
        }
        return Optional.empty();
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page) {
        return tournamentDao.findTournaments(tournamentFilter, page);
    }

    @Override
    public List<Tournament> findGameTournaments(Long game_id){
        return tournamentDao.findGameTournaments(game_id);
    }


    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image, Boolean openInscriptions, Boolean isFinished) {
        Long image_id = imageDao.insertImage(image);
        return tournamentDao.create(creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, openInscriptions, isFinished);
    }

    @Override
    public Optional<Structure> getTournamentStructure(Long tournament_id){
    	return tournamentDao.getTournamentStructure(tournament_id);
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

            int gLocal   = (m.getLocalId()   != null) ? userGroup.getOrDefault(m.getLocalId(), 0)   : 0;
            int gVisitor = (m.getVisitorId() != null) ? userGroup.getOrDefault(m.getVisitorId(), 0) : 0;
            int group = (gLocal > 0 && gLocal == gVisitor) ? gLocal : 0;

            result.computeIfAbsent(group, g -> new TreeMap<>())
                    .computeIfAbsent(stage, s -> new ArrayList<>())
                    .add(m);
        }
        return result;
    }

    @Override
    public Map<Integer, List<ParticipantUserInfo>> getTournamentParticipantsByGroup(Long tournamentId) {
        List<User> users = tournamentDao.getTournamentUsers(tournamentId);
        Map<Long, User> usersById = new HashMap<>();
        for (User u : users) {
            usersById.put(u.getId(), u);
        }

        List<ParticipantUser> participants = participantDao.getTournamentParticipantUsers(tournamentId);

        Map<Integer, List<ParticipantUserInfo>> byGroup = new TreeMap<>();
        for (ParticipantUser p : participants) {
            User u = usersById.get(p.getUser_id());
            if (u == null) {
                continue;
            }
            Integer group = (p.getGroupNumber() != null) ? p.getGroupNumber() : 0;
            ParticipantUserInfo info = new ParticipantUserInfo(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    p.getPoints(),
                    group
            );

            byGroup.computeIfAbsent(group, g -> new ArrayList<>()).add(info);
        }
        for (List<ParticipantUserInfo> list : byGroup.values()) {
            list.sort((a, b) -> {
                int cmp = b.getPoints().compareTo(a.getPoints());
                if (cmp != 0) {
                    return cmp;
                }
                return a.getUsername().compareToIgnoreCase(b.getUsername());
            });
        }
        return byGroup;
    }

    @Override
    public List<Tournament> findByCreator(Long creator_id) {
        if(findById(creator_id).isEmpty()){
            throw new UserNotFoundException();
        }
        return tournamentDao.findByCreator(creator_id);
    }

    @Override
    public void setFinished(Long tournament_id, Long match_id) {
        tournamentDao.setFinished(tournament_id, match_id);
    }

    @Override
    public void closeInscriptions(Long tournament_id){
        tournamentDao.closeInscriptions(tournament_id, participantDao.getTournamentParticipantUsers(tournament_id));
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
        tournamentDao.startTournament(tournament_id, participantDao.getTournamentParticipantUsers(tournament_id));
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2){
        tournamentDao.swapGroups(tournament_id, user1, user2);
    }

    @Override
    public void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2){
        tournamentDao.swapMatchesMembers(tournament_id, match1, match2, user1, user2);
    }

    @Override
    public Map<Game, List<Tournament>> getUnfilteredTournamentPages(Long page) {
        Map<Long, List<Tournament>> mapWithGameIdAsKey = tournamentDao.getUnfilteredTournamentPages(page);
        Map<Game, List<Tournament>> mapWithGameAsKey = new HashMap<>();
        for(Long gameId : mapWithGameIdAsKey.keySet()) {
            mapWithGameAsKey.putIfAbsent(gameDao.findById(gameId).get(), mapWithGameIdAsKey.get(gameId));
        }
        return mapWithGameAsKey;
    }

    @Override
    public Integer getPageAmount(Integer pageSize, TournamentFilter tf){
        return tournamentDao.getPageAmount(pageSize, tf);
    }

    @Override
    public void updateTournamentInfo(Long tournament_id, String name, LocalDate start_date, LocalDate end_date, Integer max_participants, byte[] image){
        Tournament t = findById(tournament_id).orElse(null);
        if(t != null){
            if(image != null){
                imageDao.updateImage(t.getImage_id(), image);
            }
            tournamentDao.updateTournamentInfo(tournament_id, name, start_date, end_date, max_participants);
        }
    }

    @Override
    public int tournamentParticipantsCount(Long tournamentId) {
    	return tournamentDao.tournamentParticipantsCount(tournamentId);
    }

    @Override
    public List<Tournament> getCreatedAndFinishedTournaments(Long userId) {
        List<Tournament> allCreatedTournaments = findByCreator(userId);
        return allCreatedTournaments.stream().filter(t -> t.getFinished()).toList();
    }

    @Override
    public List<Tournament> getCreatedAndOngoingTournaments(Long userId) {
        List<Tournament> allCreatedTournaments = findByCreator(userId);
        return allCreatedTournaments.stream().filter(t -> !t.getFinished()).toList();
    }
}
