package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.Match.PointsPair;
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
    private final RulesDao rulesDao;

    public TournamentServiceImpl(TournamentDao tournamentDao, ImageDao imageDao, ParticipantDao participantDao, MatchDao matchDao, GameDao gameDao, UserDao userDao, MailService ms, GameFormatDao gameFormatDao, RulesDao rulesDao) {
        this.tournamentDao = tournamentDao;
        this.imageDao = imageDao;
        this.participantDao = participantDao;
        this.matchDao = matchDao;
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.ms = ms;
        this.gameFormatDao = gameFormatDao;
        this.rulesDao = rulesDao;
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
        Long gameId = tournamentFilter.getGameId();
        if(gameId != null && gameDao.findById(gameId).isEmpty()) {
            LOGGER.error("Game with ID {} does not exist", gameId);
            throw new GameNotFoundException();
        }
        return tournamentDao.findTournaments(tournamentFilter, page);
    }

    @Transactional
    @Override
    public Tournament create(Long creatorId, String name, Long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format, Structure structure, Integer maxParticipants, byte[] image, Boolean openInscriptions, Boolean isFinished, Long formatId, byte[] rules) {
        Long imageId = imageDao.insertImage(image);
        Long rulesId = null;
        if (rules != null){
            rulesId = rulesDao.insertRules(rules).getId();
        }
        Tournament toReturn = tournamentDao.create(creatorId, name, gameId, region, elo, startDate, endDate, format, structure, maxParticipants, imageId, openInscriptions, isFinished, formatId, rulesId);
        User creator = userDao.findById(creatorId).orElseThrow(UserNotFoundException::new);
        ms.sendTournamentCreatedEmail(toReturn.getId(), creator.getUsername(), name, creator.getEmail());
        LOGGER.info("Tournament {} has been successfully created", name);
        return toReturn;
    }

    @Override
    public List<Tournament> findByCreator(Long creatorId, Long page, Boolean isFinished) {
        return tournamentDao.findByCreator(creatorId, page, isFinished);
    }

    @Transactional
    @Override
    public void setFinished(Long tournamentId, Long lastMatchId) { // TODO: check for possible error handling
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Long winner;
        if (tournament.getStructure() == Structure.LEAGUE) {
            List<Participant> tops = getLeagueTournamentTopPositions(tournamentId);
            if(tops.isEmpty()) {
                LOGGER.debug("Top positions list for league format is empty");
            }
            if (tops.size() > 1) {
                createMatchesLeague(tournament, tops, matchDao.getMaxMatchId(tournamentId) + 1, matchDao.getTournamentMaxStage(tournamentId) + 1, null);
                return;
            }
            winner = tops.getFirst().getId();
        } else {
            winner = matchDao.getMatchWinner(tournamentId, lastMatchId);
        }
        tournamentDao.setTournamentWinner(tournamentId, winner);
        LOGGER.info("User with ID {} has won the tournament with ID {}", winner, tournamentId);
        tournamentDao.setFinished(tournamentId);
        LOGGER.info("Tournament with ID {} has successfully ended", tournamentId);
        List<Participant> participantUsers = participantDao.getTournamentParticipantUsers(tournamentId);
        Integer teamSize = getPlayersPerTeam(tournamentId);
        if(teamSize > 1) {
            List<Participant> participantTeams = participantDao.getTournamentParticipantTeams(tournamentId);
            Long winnerTeam = null;
            for(Participant team : participantTeams){
                if(team.getId().equals(winner)) {
                    winnerTeam = team.getTeam().getId();
                    break;
                }
            }
            for(Participant p : participantUsers){
                User user = userDao.findById(p.getUser().getId()).orElseThrow(UserNotFoundException::new);
                if(p.getTeam().getId().equals(winnerTeam)) {
                    ms.sendTournamentWinnerEmail(tournamentId, user.getUsername(), tournament.getName(), user.getEmail());
                } else {
                    ms.sendTournamentEndedEmail(tournamentId, user.getUsername(), tournament.getName(), user.getEmail());
                }
            }
        }else {
            for(Participant p : participantUsers){
                User user = userDao.findById(p.getUser().getId()).orElseThrow(UserNotFoundException::new);
                if(p.getId().equals(winner)) {
                    ms.sendTournamentWinnerEmail(tournamentId, user.getUsername(), tournament.getName(), user.getEmail());
                } else {
                    ms.sendTournamentEndedEmail(tournamentId, user.getUsername(), tournament.getName(), user.getEmail());
                }
            }
        }
    }

    private List<Participant> getLeagueTournamentTopPositions(Long tournamentId){
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        PointsPair maxPoints = participantDao.getTournamentMaxPointsPairGroup(tournamentId, null);
        if (maxPoints == null) return java.util.Collections.emptyList();
        return participantDao.getTournamentParticipantsByPointsPair(tournamentId, null, maxPoints,  gameFormatDao.findById(tournament.getFormatId()).orElseThrow(GameFormatNotFoundException::new).getPlayersPerTeam());
    }

    @Transactional
    @Override
    public void closeInscriptions(Long tournamentId){
        if(tournamentDao.isClosed(tournamentId)) {
            LOGGER.warn("The inscriptions for the tournament with ID {} have already been closed", tournamentId);
            throw new TournamentAlreadyClosedException();
        }
        Integer teamSize = getPlayersPerTeam(tournamentId);
        List<Participant> participants;
        List<Participant> teams = new ArrayList<>();
        participants = participantDao.getTournamentParticipantUsers(tournamentId);
        if (teamSize > 1) {
            teams = participantDao.getTournamentParticipantTeams(tournamentId);
        }
        createMatches(tournamentId, teams.isEmpty() ? participants : teams);
        tournamentDao.closeInscriptions(tournamentId);
        ms.sendListEmail(tournamentId, participants);
        LOGGER.info("The inscriptions for the tournament with ID {} have been successfully closed", tournamentId);
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
    public void startTournament(Long tournamentId){
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if(tournament.getTournamentStarted()) {
            LOGGER.warn("The tournament with ID {} has already started", tournamentId);
            throw new TournamentAlreadyStartedException();
        }
        tournamentDao.startTournament(tournamentId);
        LOGGER.info("The tournament with ID {} has successfully been started", tournamentId);
        if(tournament.getStructure().equals(Structure.HYBRID) && tournament.getIsGroupStage()){
            createGroupStageMatches(tournament);
        }
        List<Participant> participants = participantDao.getTournamentParticipantUsers(tournamentId);
        Optional<User> optCreator = userDao.findById(tournament.getCreatorId());
        if(optCreator.isEmpty()) {
            LOGGER.error("User with ID {} does not exist", tournament.getCreatorId());
            throw new UserNotFoundException();
        }
        User creator = optCreator.get();
        for(Participant p : participants) {
            User participant = userDao.findById(p.getUser().getId()).orElseThrow(UserNotFoundException::new);
            ms.sendTournamentStartedEmail(tournamentId, participant.getUsername(), tournament.getName(), creator.getEmail(), participant.getEmail());
        }
    }

    @Transactional
    @Override
    public Map<Game, List<Tournament>> getUnfilteredTournamentPages(Long page) {
        Map<Long, List<Tournament>> mapWithGameIdAsKey = tournamentDao.getUnfilteredTournamentPages(page);
        Map<Game, List<Tournament>> mapWithGameAsKey = new HashMap<>();
        for(Long gameId : mapWithGameIdAsKey.keySet()) {
            mapWithGameAsKey.putIfAbsent(gameDao.findById(gameId).orElseThrow(GameNotFoundException::new), mapWithGameIdAsKey.get(gameId));
        }
        return mapWithGameAsKey;
    }

    @Override
    public Integer getPageAmount(Integer pageSize, TournamentFilter tf){
        return tournamentDao.getPageAmount(pageSize, tf);
    }

    @Transactional
    @Override
    public void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, byte[] image){
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if(image != null){
            imageDao.updateImage(tournament.getImageId(), image);
        }
        tournamentDao.updateTournamentInfo(tournamentId, name, startDate, endDate, maxParticipants);
        LOGGER.info("The tournament with ID {} has successfully been updated", tournamentId);
    }

    @Override
    public int getTournamentParticipantsCount(Long tournamentId) {
    	return tournamentDao.getTournamentParticipantsCount(tournamentId);
    }

    private void createMatches(Long tournamentId, List<Participant> participants) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if (!participants.isEmpty()) { // TODO: no participant error handling
            if(tournament.getStructure().equals(Structure.ELIMINATION)) {
                createMatchesBracket(tournament, participants, 1L, null);
            } else if (tournament.getStructure().equals(Structure.HYBRID)) {
                createMatchesHybrid(tournament, participants);
            } else {
                createMatchesLeague(tournament, participants);
            }
        }
        LOGGER.info("Matches for tournament with ID {} of {} format have successfully been created", tournament.getId(), tournament.getStructure());
    }

    private void createMatchesLeague(Tournament t, List<Participant> participants) {
        createMatchesLeague(t, participants, 1L, 1, null);
    }

    private void createMatchesLeague(Tournament t, List<Participant> participants, Long firstMatchId, Integer firstStage, Boolean isGroupStage) {
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
        Integer teamSize = gameFormatDao.findById(t.getFormatId()).orElseThrow(GameFormatNotFoundException::new).getPlayersPerTeam();
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
    public void createBracketFromGroups(Long tournamentId) { // TODO: error handling here?
        Integer groups = participantDao.getTournamentGroups(tournamentId);
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        List<Participant> classified = new ArrayList<>(groups * 2);
        for(int i = 1; i <= groups; i++){
            Map<Integer, List<Participant>> topPositions = getGroupTopPositions(tournamentId, i);
            Long maxMatchId = matchDao.getMaxMatchId(tournamentId);
            if(topPositions.get(1).size() > 1){
                createMatchesLeague(tournament, topPositions.get(1), maxMatchId + 1, matchDao.getTournamentGroupMaxStage(tournamentId, i) + 1, true);
                return;
            }else if(topPositions.get(2).size() > 1){
                participantDao.sumPoints(tournamentId, topPositions.get(1).getFirst().getId(), 3 * (topPositions.get(2).size() / 2) + 1, 0, getPlayersPerTeam(tournamentId));
                createMatchesLeague(tournament, topPositions.get(2), maxMatchId + 1, matchDao.getTournamentGroupMaxStage(tournamentId, i) + 1, true);
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
        createMatchesBracket(tournament, classified, matchDao.getMaxMatchId(tournamentId) + 1, false);
    }

    private Map<Integer, List<Participant>> getGroupTopPositions(Long tournamentId, Integer groupNumber){
        Map<Integer, List<Participant>> out = new HashMap<>();
        Optional<Tournament> optTournament = tournamentDao.findById(tournamentId);
        if(optTournament.isEmpty()) {
            return out;
        }
        Tournament tournament = optTournament.get();
        Integer teamSize;
        if(tournament.getFormatId() != null){
            teamSize = gameFormatDao.findById(optTournament.get().getFormatId()).orElseThrow(GameFormatNotFoundException::new).getPlayersPerTeam();
        }else{
            teamSize = 1;
        }
        out.put(1, participantDao.getTournamentParticipantsByPointsPair(tournamentId, groupNumber, participantDao.getTournamentMaxPointsPairGroup(tournamentId, groupNumber), teamSize));
        out.put(2, participantDao.getTournamentParticipantsByPointsPair(tournamentId, groupNumber, participantDao.getTournamentSecondMaxPointsPairGroup(tournamentId, groupNumber), teamSize));
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
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if(tournament.getFormatId() != null){
            return gameFormatDao.getPlayersPerTeam(tournament.getFormatId());
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
    public List<Tournament> getUserWonTournament(Long userId, Long page) {
        return tournamentDao.getUserWonTournament(userId, page);
    }

    @Override
    public Integer getUserWonTournamentPages(Long userId) {
        return tournamentDao.getUserWonTournamentPages(userId);
    }


    @Override
    public List<Tournament> getCreatedAndFinishedTournaments(Long userId, Long page) {
        return findByCreator(userId, page, true);
    }

    @Override
    public List<Tournament> getCreatedAndOngoingTournaments(Long userId, Long page) {
        return findByCreator(userId, page, false);
    }

    @Override
    public void contactOwner(Long tournamentId, User currentUser, String subject, String body, Long creatorId) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        User creator = userDao.findById(creatorId).orElseThrow(TournamentNotFoundException::new);
        ms.sendContactOwnerEmail(tournament, currentUser, subject, body, creator);
    }

    @Transactional
    @Override
    public void updateTouramentRating(Long tournamentId, Float userRating) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Float currentRating = tournament.getRating();

        Float newRating = (currentRating == null) ? userRating : (currentRating + userRating) / 2;

        tournamentDao.updateTournamentRating(tournamentId, newRating);
        LOGGER.debug("Tournament {} rating updated to {}", tournament.getName(), newRating);
    }

}
