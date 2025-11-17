package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.Match.PointsPair;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.hibernate.Hibernate;
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
    public Optional<Tournament> findById(long id) {
        Optional<Tournament> toReturn = tournamentDao.findById(id);
        if(toReturn.isEmpty()) {
            LOGGER.error("Tournament with ID {} does not exist", id);
            throw new TournamentNotFoundException();
        }
        return toReturn;
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter tournamentFilter, long page) {
        Long gameId = tournamentFilter.getGameId();
        if(gameId != null && gameDao.findById(gameId).isEmpty()) {
            LOGGER.error("Game with ID {} does not exist", gameId);
            throw new GameNotFoundException();
        }
        return tournamentDao.findTournaments(tournamentFilter, page);
    }

    @Transactional
    @Override
    public Tournament create(long creatorId, String name, long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format,
                             Structure structure, int maxParticipants, byte[] image, boolean openInscriptions, boolean isFinished, long formatId,
                             byte[] rules, String serverName, String serverPassword, String discordChannel) {
        Long imageId = imageDao.insertImage(image);
        if(imageId == null) {
            throw new ImageNotFoundException();
        }
        Long rulesId = null;
        if (rules != null){
            rulesId = rulesDao.insertRules(rules).getId();
        }
        Tournament toReturn = tournamentDao.create(creatorId, name, gameId, region, elo, startDate, endDate, format, structure, maxParticipants,
                imageId, openInscriptions, isFinished, formatId, rulesId, serverName, serverPassword, discordChannel);
        User creator = userDao.findById(creatorId).orElseThrow(UserNotFoundException::new);
        ms.sendTournamentCreatedEmail(toReturn.getId(), creator.getUsername(), name, creator.getEmail());
        LOGGER.info("Tournament {} has been successfully created", name);
        return toReturn;
    }

    @Transactional
    @Override
    public void setFinished(long tournamentId, long lastMatchId) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Long winner;
        if (tournament.getStructure() == Structure.LEAGUE) {
            List<Participant> tops = getLeagueTournamentTopPositions(tournamentId);
            if(tops.isEmpty()) {
                LOGGER.debug("Top positions list for league format is empty");
            }
            if (tops.size() > 1) {
                Long maxMatchId = matchDao.getMaxMatchId(tournamentId);
                if(maxMatchId == null) {
                    throw new MatchNotFoundException();
                }
                int maxTournamentStage = matchDao.getTournamentMaxStage(tournamentId);
                createMatchesLeague(tournament, tops, maxMatchId + 1,  maxTournamentStage + 1, null);
                return;
            }
            winner = tops.getFirst().getId();
        } else {
            winner = matchDao.getMatchWinner(tournamentId, lastMatchId);
        }
        if(winner == null) {
            throw new MissingWinnerException();
        }
        tournamentDao.setTournamentWinner(tournamentId, winner);
        LOGGER.info("User with ID {} has won the tournament with ID {}", winner, tournamentId);
        tournamentDao.setFinished(tournamentId);
        LOGGER.info("Tournament with ID {} has successfully ended", tournamentId);
        List<Participant> participantUsers = participantDao.getTournamentParticipantUsers(tournamentId);
        GameFormat format = tournament.getFormatEntity();
        int teamSize;
        if(format == null) {
            teamSize = 1;
        }else{
            teamSize = format.getPlayersPerTeam();
        }
        if(teamSize > 1) {
            List<Participant> participantTeams = participantDao.getTournamentParticipantTeams(tournamentId);
            Long winnerTeam = null;
            for(Participant team : participantTeams){
                if(team.getId().equals(winner)) {
                    winnerTeam = team.getTeam().getId();
                    break;
                }
            }
            if(winnerTeam == null) {
                throw new MissingWinnerException();
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

    private List<Participant> getLeagueTournamentTopPositions(long tournamentId){
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        PointsPair maxPoints = participantDao.getTournamentMaxPointsPairGroup(tournamentId, null);
        if (maxPoints == null) return java.util.Collections.emptyList();
        return participantDao.getTournamentParticipantsByPointsPair(tournamentId, null, maxPoints,  gameFormatDao.findById(tournament.getFormatId()).orElseThrow(GameFormatNotFoundException::new).getPlayersPerTeam());
    }

    @Transactional
    @Override
    public void closeInscriptions(long tournamentId){
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if(!tournament.getOpenInscriptions()) {
            LOGGER.warn("The inscriptions for the tournament with ID {} have already been closed", tournamentId);
            throw new TournamentAlreadyClosedException();
        }
        GameFormat format = tournament.getFormatEntity();
        int teamSize;
        if(format == null) {
            teamSize = 1;
        }else{
            teamSize = format.getPlayersPerTeam();
        }
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
    public List<Tournament> searchByName(String name, long page){
        return tournamentDao.searchByName(name, page);
    }

    @Override
    public int countSearchByName(String name) {
        return tournamentDao.countSearchByName(name);
    }

    @Transactional
    @Override
    public void startTournament(long tournamentId){
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
            ms.sendTournamentStartedEmail(tournament, participant.getUsername(), creator.getEmail(), participant.getEmail());
        }
    }

    @Transactional
    @Override
    public Map<Game, List<Tournament>> getUnfilteredTournamentPages(long page) {
        Map<Long, List<Tournament>> mapWithGameIdAsKey = tournamentDao.getUnfilteredTournamentPages(page);
        Map<Game, List<Tournament>> mapWithGameAsKey = new HashMap<>();
        for(Long gameId : mapWithGameIdAsKey.keySet()) {
            if(gameId == null) {
                throw new GameNotFoundException();
            }
            mapWithGameAsKey.putIfAbsent(gameDao.findById(gameId).orElseThrow(GameNotFoundException::new), mapWithGameIdAsKey.get(gameId));
        }
        return mapWithGameAsKey;
    }

    @Override
    public int getPageAmount(int pageSize, TournamentFilter tf){
        return tournamentDao.getPageAmount(pageSize, tf);
    }

    @Transactional
    @Override
    public void updateTournamentInfo(long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, byte[] image, String serverName, String serverPassword, String discordChannel){
        Tournament t = findById(tournamentId).orElse(null);
        if(t != null){
            if(image != null){
                Long imageId = t.getImageId();
                if(imageId == null) {
                    throw new ImageNotFoundException();
                }
                imageDao.updateImage(imageId, image);
            }
            tournamentDao.updateTournamentInfo(tournamentId, name, startDate, endDate, maxParticipants, serverName, serverPassword, discordChannel);
            if(t.getTournamentStarted()){
                if(serverName != null || serverPassword != null || discordChannel != null){
                    List<Participant> participants = participantDao.getTournamentParticipantUsers(tournamentId);
                    for(Participant p : participants) {
                        User participant = userDao.findById(p.getUser().getId()).orElseThrow(TournamentNotFoundException::new);
                        if(serverName != null || serverPassword != null){
                            ms.sendServerInfoUpdated(t, participant.getUsername(), participant.getEmail());
                        }else {
                            ms.sendDiscordLinkUpdated(t, participant.getUsername(), participant.getEmail());
                        }
                    }
                }
            }
            LOGGER.info("The tournament with ID {} has successfully been updated", tournamentId);
        }
    }

    @Override
    public int getTournamentParticipantsCount(long tournamentId) {
    	return tournamentDao.getTournamentParticipantsCount(tournamentId);
    }

    private void createMatches(long tournamentId, List<Participant> participants) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if (!participants.isEmpty()) {
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

    private void createMatchesLeague(Tournament t, List<Participant> participants, long firstMatchId, int firstStage, Boolean isGroupStage) {
        int n = participants.size();

        if (n % 2 != 0) {
            participants.add(null);
            n++;
        }
        int totalRounds = n - 1;
        long matchId = firstMatchId;

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

    private void createMatchesBracket(Tournament t, List<Participant> participants, long firstMatchId, Boolean isGroupStage) {
        int n = participants.size();

        int floorPowerOfTwo = 1;
        while (floorPowerOfTwo * 2 <= n) {
            floorPowerOfTwo *= 2;
        }
        int extras = n - floorPowerOfTwo;

        long matchId = firstMatchId;
        int stage = matchDao.getTournamentMaxStage(t.getId()) + 1;

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
                participantDao.updateGroupNumberForUsers(t.getId(), groupNumber, Arrays.asList(ids));
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
    public void createBracketFromGroups(long tournamentId) {
        int groups = participantDao.getTournamentGroups(tournamentId);
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        GameFormat format = tournament.getFormatEntity();
        int teamSize;
        if(format == null) {
            teamSize = 1;
        }else{
            teamSize = format.getPlayersPerTeam();
        }
        List<Participant> classified = new ArrayList<>(groups * 2);
        for(int i = 1; i <= groups; i++){
            Map<Integer, List<Participant>> topPositions = getGroupTopPositions(tournamentId, i);
            Long maxMatchId = matchDao.getMaxMatchId(tournamentId);
            if(maxMatchId == null) {
                throw new MatchNotFoundException();
            }
            if(topPositions.get(1).size() > 1){
                createMatchesLeague(tournament, topPositions.get(1), maxMatchId + 1, matchDao.getTournamentGroupMaxStage(tournamentId, i) + 1, true);
                return;
            }else if(topPositions.get(2).size() > 1){
                participantDao.sumPoints(tournamentId, topPositions.get(1).getFirst().getId(), 3 * (topPositions.get(2).size() / 2) + 1, 0, teamSize);
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
        Long finalMaxMatchId = matchDao.getMaxMatchId(tournamentId);
        if(finalMaxMatchId == null) {
            throw new MatchNotFoundException();
        }
        createMatchesBracket(tournament, classified, finalMaxMatchId + 1, false);
    }

    private Map<Integer, List<Participant>> getGroupTopPositions(long tournamentId, int groupNumber){
        Map<Integer, List<Participant>> out = new HashMap<>();
        Optional<Tournament> optTournament = tournamentDao.findById(tournamentId);
        if(optTournament.isEmpty()) {
            return out;
        }
        Tournament tournament = optTournament.get();
        int teamSize;
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
    public void contactOwner(long tournamentId, User currentUser, String subject, String body, long creatorId) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        User creator = userDao.findById(creatorId).orElseThrow(TournamentNotFoundException::new);
        ms.sendContactOwnerEmail(tournament, currentUser, subject, body, creator);
    }

    @Transactional
    @Override
    public void updateTournamentRating(long tournamentId, float userRating) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Float currentRating = tournament.getRating();

        float newRating = (currentRating == null) ? userRating : (currentRating + userRating) / 2;

        tournamentDao.updateTournamentRating(tournamentId, newRating);
        LOGGER.debug("Tournament {} rating updated to {}", tournament.getName(), newRating);
    }

    @Override
    public void notifyCreatorOfLeavingUser(User user, long tournamentId) {
        Tournament tournament = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        ms.sendTournamentAbandonedEmail(tournament.getCreator(), user, tournament);
    }

    @Override
    public List<Tournament> findUserTournaments(long userId, boolean isFinished, boolean isCreator, boolean won, long page) {
        return tournamentDao.findUserTournaments(userId, isFinished, isCreator, won, page);
    }

    @Override
    public int countUserTournaments(long userId, boolean isFinished, boolean isCreator, boolean won) {
        return tournamentDao.countUserTournaments(userId, isFinished, isCreator, won);
    }

    @Transactional
    @Override
    public GameFormat getFormat(long tournamentId){
        Tournament t = findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Hibernate.initialize(t.getFormatEntity());
        return t.getFormatEntity();
    }


}
