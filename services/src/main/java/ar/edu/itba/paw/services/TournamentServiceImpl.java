package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Transactional(readOnly = true)
@Service
public class TournamentServiceImpl implements TournamentService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TournamentServiceImpl.class);

    private final TournamentDao tournamentDao;
    private final ImageDao imageDao;
    private final ParticipantDao participantDao;
    private final MatchDao matchDao;
    private final GameDao gameDao;
    private final UserDao userDao;
    private final MailService ms;
    private final GameFormatDao gameFormatDao;

    public TournamentServiceImpl(TournamentDao tournamentDao, ImageDao imageDao, ParticipantDao participantDao, MatchDao matchDao, GameDao gameDao, UserDao userDao, MailService ms, GameFormatDao gameFormatDao) {
        this.tournamentDao = tournamentDao;
        this.imageDao = imageDao;
        this.participantDao = participantDao;
        this.matchDao = matchDao;
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.ms = ms;
        this.gameFormatDao = gameFormatDao;
    }

    @Override
    public Optional<Tournament> findById(Long id) { // TODO: this should return Tournament. Check for uses of this function throughout
        Optional<Tournament> toReturn = tournamentDao.findById(id);
        if(toReturn.isEmpty()) {
            LOGGER.error("Tournament with ID {} does not exist", id);
            throw new TournamentNotFoundException();
        }
        return toReturn;
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page) {
        Long gameId = tournamentFilter.getGame_id();
        if(gameDao.findById(gameId).isEmpty()) {
            LOGGER.error("Game with ID {} does not exist", gameId);
            throw new GameNotFoundException();
        }
        return tournamentDao.findTournaments(tournamentFilter, page);
    }

    @Override
    public List<Tournament> findGameTournaments(Long game_id){
        return tournamentDao.findGameTournaments(game_id);
    }

    @Transactional
    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image, Boolean openInscriptions, Boolean isFinished, Long format_id) {
        Long image_id = imageDao.insertImage(image);
        Tournament toReturn = tournamentDao.create(creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, openInscriptions, isFinished, format_id);
        User creator = userDao.findById(creator_id).get();
        ms.sendTournamentCreatedEmail(toReturn.getId(), creator.getUsername(), name, creator.getEmail());
        LOGGER.info("Tournament {} has been successfully created", name);
        return toReturn;
    }

    @Override
    public List<Tournament> findByCreator(Long creator_id, Long page) {
        return tournamentDao.findByCreator(creator_id, page);
    }

    @Transactional
    @Override
    public void setFinished(Long tournament_id, Long lastMatchId) { // TODO: check for possible error handling
        Tournament t = findById(tournament_id).orElse(null);
        Long winner = null;
        if (t != null) {
            if (t.getStructure() == Structure.LEAGUE) {
                List<Participant> tops = getLeagueTournamentTopPositions(tournament_id);
                if(tops.isEmpty()) {
                    LOGGER.debug("Top positions list for league format is empty");
                }
                if (tops.size() > 1) {
                    createMatchesLeague(t, tops, lastMatchId + 1, matchDao.getTournamentMaxStage(tournament_id) + 1, null);
                    return;
                }
                winner = tops.getFirst().getId();
            } else {
                winner = matchDao.getMatchWinner(tournament_id, lastMatchId);
            }
            tournamentDao.setTournamentWinner(tournament_id, winner);
            LOGGER.info("User with ID {} has won the tournament with ID {}", winner, tournament_id);
            tournamentDao.setFinished(tournament_id);
            LOGGER.info("Tournament with ID {} has successfully ended", tournament_id);
        }
        List<Participant> participants = participantDao.getTournamentParticipantUsers(tournament_id);
        for(Participant p : participants){
            User user = userDao.findById(p.getId()).get();
            if(p.getId().equals(winner)) {
                ms.sendTournamentWinnerEmail(tournament_id, user.getUsername(), t.getName(), user.getEmail());
            } else {
                ms.sendTournamentEndedEmail(tournament_id, user.getUsername(), t.getName(), user.getEmail());
            }
        }
    }

    private List<Participant> getLeagueTournamentTopPositions(Long tournamentId){
        Optional<Tournament> t = tournamentDao.findById(tournamentId);
        Integer maxPoints = participantDao.getTournamentMaxPointsGroup(tournamentId, null);
        if (maxPoints == null) return java.util.Collections.emptyList();
        return participantDao.getTournamentParticipantsByPoints(tournamentId, null, maxPoints,  gameFormatDao.getFormatById(t.get().getFormat_id()).get().getPlayers_per_team());
    }

    @Transactional
    @Override
    public void closeInscriptions(Long tournament_id){
        if(tournamentDao.isClosed(tournament_id)) {
            LOGGER.warn("The inscriptions for the tournament with ID {} have already been closed", tournament_id);
            throw new TournamentAlreadyClosedException();
        }
        Integer teamSize = getPlayersPerTeam(tournament_id);
        List<Participant> participants;
        if (teamSize > 1){
            participants = participantDao.getTournamentParticipantTeams(tournament_id);
        }else {
            participants = participantDao.getTournamentParticipantUsers(tournament_id);
        }
        createMatches(tournament_id, participants);
        tournamentDao.closeInscriptions(tournament_id);
        LOGGER.info("The inscriptions for the tournament with ID {} have been successfully closed", tournament_id);
    }

    @Override
    public List<Tournament> findUserActiveTournaments(Long userId, Long page) {
        return tournamentDao.findUserActiveTournaments(userId, page);
    }

    @Override
    public List<Tournament> findUserPastTournaments(Long userId, Long page) {
        return tournamentDao.findUserPastTournaments(userId, page);
    }

    @Override
    public List<Tournament> searchByName(String name){
        return tournamentDao.searchByName(name);
    }

    @Transactional
    @Override
    public void startTournament(Long tournament_id){
        Optional<Tournament> optTournament = findById(tournament_id);
        if(optTournament.get().getTournamentStarted()) {
            LOGGER.warn("The tournament with ID {} has already started", tournament_id);
            throw new TournamentAlreadyStartedException();
        }
        tournamentDao.startTournament(tournament_id);
        LOGGER.info("The tournament with ID {} has successfully been started", tournament_id);
        Tournament t = optTournament.get();
        if(t.getStructure().equals(Structure.HYBRID) && t.getIs_group_stage()){
            createGroupStageMatches(t);
        }
        List<Participant> participants = participantDao.getTournamentParticipantUsers(tournament_id);
        User creator = userDao.findById(t.getCreator_id()).get();
        for(Participant p : participants) {
            User participant = userDao.findById(p.getId()).get();
            ms.sendTournamentStartedEmail(tournament_id, participant.getUsername(), t.getName(), creator.getEmail(), participant.getEmail());
        }
    }

    @Transactional
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

    @Transactional
    @Override
    public void updateTournamentInfo(Long tournament_id, String name, LocalDate start_date, LocalDate end_date, Integer max_participants, byte[] image){
        Tournament t = findById(tournament_id).orElse(null);
        if(t != null){
            if(image != null){
                imageDao.updateImage(t.getImage_id(), image);
            }
            tournamentDao.updateTournamentInfo(tournament_id, name, start_date, end_date, max_participants);
            LOGGER.info("The tournament with ID {} has successfully been updated", tournament_id);
        }
    }

    @Override
    public int getTournamentParticipantsCount(Long tournamentId) {
    	return tournamentDao.getTournamentParticipantsCount(tournamentId);
    }

    private void createMatches(Long tournamentId, List<Participant> participants) {
        Tournament t = findById(tournamentId).orElse(null);
        if (t != null && !participants.isEmpty()) { // TODO: no participant error handling
            if(t.getStructure().equals(Structure.ELIMINATION)) {
                createMatchesBracket(t, participants, 1L, null);
            } else if (t.getStructure().equals(Structure.HYBRID)) {
                createMatchesHybrid(t, participants);
            } else {
                createMatchesLeague(t, participants);
            }
        }
        LOGGER.info("Matches for tournament with ID {} of {} format have successfully been created", t.getId(), t.getStructure());
    }

    public void createMatchesLeague(Tournament t, List<Participant> participants) {
        createMatchesLeague(t, participants, 1L, 1, null);
    }

    public void createMatchesLeague(Tournament t, List<Participant> participants, Long firstMatchId, Integer firstStage, Boolean isGroupStage) {
        int n = participants.size();

        if (n % 2 != 0) {
            participants.add(null);
            n++;
        }
        int totalRounds = n - 1;
        Long matchId = firstMatchId;

        List<Participant> rotated = new ArrayList<>(participants);

        for (int round = firstStage; round < firstStage + totalRounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                Participant home = rotated.get(i);
                Participant away = rotated.get(n - 1 - i);

                if (home != null && away != null) {
                    matchDao.insertMatch(matchId++, t.getId(), home.getId(), away.getId(), round,null, null, null, isGroupStage);
                }
            }
            Participant first = rotated.remove(1);
            rotated.add(first);
        }
    }

    private void createMatchesBracket(Tournament t, List<Participant> participants, Long firstMatchId, Boolean isGroupStage) {
        int n = participants.size();

        int floorPowerOfTwo = 1;
        while (floorPowerOfTwo * 2 <= n) {
            floorPowerOfTwo *= 2;
        }
        int extras = n - floorPowerOfTwo;

        Long matchId = firstMatchId;
        Integer stage = matchDao.getTournamentMaxStage(t.getId()) + 1;

        List<Participant> nextRound = new ArrayList<>();

        for (int i = 0; i < extras; i++) {
            Participant home = participants.get(i * 2);
            Participant away = participants.get(i * 2 + 1);
            Long homeId = (home != null) ? home.getId() : null;
            Long awayId = (away != null) ? away.getId() : null;

            matchDao.insertMatch(matchId++, t.getId(), homeId, awayId, stage, null,null, null, isGroupStage);
            nextRound.add(null);
        }

        for (int i = extras * 2; i < n; i++) {
            nextRound.add(participants.get(i));
        }

        stage++;

        while (nextRound.size() > 1) {
            List<Participant> currentRound = nextRound;
            nextRound = new ArrayList<>();

            for (int i = 0; i < currentRound.size(); i += 2) {
                Participant home = currentRound.get(i);
                Participant away = (i + 1 < currentRound.size()) ? currentRound.get(i + 1) : null;
                Long homeId = (home != null) ? home.getId() : null;
                Long awayId = (away != null) ? away.getId() : null;
                matchDao.insertMatch(matchId++, t.getId(), homeId, awayId, stage,null, null, null, isGroupStage);
                nextRound.add(null);
            }

            stage++;
        }
    }

    private void createMatchesHybrid(Tournament t, List<Participant> participants) {
        int n = participants.size();
        Integer teamSize = gameFormatDao.getFormatById(t.getFormat_id()).get().getPlayers_per_team();
        if (n > 8){
            tournamentDao.setIsGroupStage(t.getId(), true);
            int groupsCount = calculateGroups(n);
            List<Integer> distribution = distributeParticipants(n);
            int index = 0;
            for (int g = 0; g < groupsCount; g++) {
                int size = distribution.get(g);
                List<Participant> group = new ArrayList<>(participants.subList(index, index + size));
                Long[] ids = group.stream().map(Participant::getId).toArray(Long[]::new);
                int groupNumber = g + 1;
                participantDao.updateGroupNumberForUsers(t.getId(), groupNumber, Arrays.asList(ids), teamSize);
                index += size;
            }
        }else{
            tournamentDao.setIsGroupStage(t.getId(), false);
            createMatchesBracket(t, participants, 1L, false);
        }
    }

    private int calculateGroups(int n) {
        int groups = Math.max(1, n / 3);   // min 3 participants per group
        groups = Math.min(groups, 16);     // máx 16  groups
        return groups;
    }

    private List<Integer> distributeParticipants(int participantsCount) {
        int groups = calculateGroups(participantsCount);

        int baseSize = participantsCount / groups;
        int extra = participantsCount % groups;

        List<Integer> distribution = new ArrayList<>();

        for (int i = 0; i < groups; i++) {
            if (i < extra) {
                distribution.add(baseSize + 1);
            } else {
                distribution.add(baseSize);
            }
        }
        return distribution;
    }

    @Transactional
    @Override
    public void createBracketFromGroups(Long tournamentId, Long lastMatchId) { // TODO: error handling here?
        Integer groups = participantDao.getTournamentGroups(tournamentId);
        Tournament t = findById(tournamentId).orElse(null);
        List<Participant> classified = new ArrayList<>(groups * 2);
        for(int i = 1; i <= groups; i++){

            Map<Integer, List<Participant>> topPositions = getGroupTopPositions(tournamentId, i);
            if(topPositions.get(1).size() > 1){
                createMatchesLeague(t, topPositions.get(1), lastMatchId + 1, matchDao.getTournamentGroupMaxStage(tournamentId, i) + 1, true);
                return;
            }else if(topPositions.get(2).size() > 1){
                participantDao.sumPoints(tournamentId, topPositions.get(1).getFirst().getId(), 3 * (topPositions.get(2).size() / 2), getPlayersPerTeam(tournamentId));
                createMatchesLeague(t, topPositions.get(2), lastMatchId + 1, matchDao.getTournamentGroupMaxStage(tournamentId, i) + 1, true);
                return;
            }
            if(topPositions.get(1).isEmpty() || topPositions.get(2).isEmpty()){
                return;
            }
            Participant local   = topPositions.get(1).getFirst();
            Participant visitor = topPositions.get(2).getFirst();
            classified.add(local);
            classified.add(visitor);
        }

        tournamentDao.setIsGroupStage(tournamentId, false);
        createMatchesBracket(findById(tournamentId).orElse(null), classified, matchDao.getMaxMatchId(tournamentId) + 1, false);
    }

    private Map<Integer, List<Participant>> getGroupTopPositions(Long tournament_id, Integer group_number){
        Map<Integer, List<Participant>> out = new HashMap<>();
        Optional<Tournament> t = tournamentDao.findById(tournament_id);
        if(t.isEmpty()) {
            return out;
        }
        Tournament tournament = t.get();
        Integer teamSize;
        if(tournament.getFormat_id() != null){
            teamSize = gameFormatDao.getFormatById(t.get().getFormat_id()).get().getPlayers_per_team();
        }else{
            teamSize = 1;
        }
        out.put(1, participantDao.getTournamentParticipantsByPoints(tournament_id, group_number, participantDao.getTournamentMaxPointsGroup(tournament_id, group_number), teamSize));
        out.put(2, participantDao.getTournamentParticipantsByPoints(tournament_id, group_number, participantDao.getTournamentSecondMaxPointsGroup(tournament_id, group_number), teamSize));
        return out;
    }

    private void createGroupStageMatches(Tournament t){
        Map<Integer, List<Participant>> groupedParticipants = getGroupedParticipants(participantDao.getTournamentParticipantUsers(t.getId()));
        if(groupedParticipants != null){
            long nextId = 1L;
            for(List<Participant> participants : groupedParticipants.values()){
                createMatchesLeague(t, participants, nextId, 1, true);
                nextId += ((long) participants.size() * (participants.size() - 1)) / 2;
            }
        }
    }

    private Map<Integer, List<Participant>> getGroupedParticipants(List<Participant> participants) {
        Map<Integer, List<Participant>> grouped = new TreeMap<>(Integer::compareTo);
        for (Participant p : participants) {
            if (p.getGroupNumber() == null) {
                return null;
            }
            grouped.computeIfAbsent(p.getGroupNumber(), k -> new ArrayList<>()).add(p);
        }
        return grouped;
    }

    @Override
    public Integer getPlayersPerTeam(Long tournamentId){
        Tournament t = findById(tournamentId).orElse(null);
        if(t != null && t.getFormat_id() != null){
            return gameFormatDao.getPlayersPerTeam(t.getFormat_id());
        }
        return null;
    }

    @Override
    public Integer getPagesBySection(Long userId, String section) {
        if (Objects.equals(section, "active")){
            return tournamentDao.getUserActiveTournamentsPages(userId);
        }if (Objects.equals(section, "finished")){
            return tournamentDao.getUserPastTournamentsPages(userId);
        }if (Objects.equals(section, "ownedFinished")){
            return tournamentDao.getCreatedAndFinishedTournamentsPages(userId);
        }if (Objects.equals(section, "ownedOngoing")){
            return tournamentDao.getCreatedAndOngoingTournamentsPages(userId);
        }
        return 1;
    }


    @Override
    public List<Tournament> getCreatedAndFinishedTournaments(Long userId, Long page) {
        List<Tournament> allCreatedTournaments = findByCreator(userId, page);
        return allCreatedTournaments.stream().filter(t -> t.getFinished()).toList();
    }

    @Override
    public List<Tournament> getCreatedAndOngoingTournaments(Long userId, Long page) {
        List<Tournament> allCreatedTournaments = findByCreator(userId, page);
        return allCreatedTournaments.stream().filter(t -> !t.getFinished()).toList();
    }

}
