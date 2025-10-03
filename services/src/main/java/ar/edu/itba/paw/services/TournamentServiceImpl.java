package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Tournament.Tournament;
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
    private final ImageDao imageDao;
    private final ParticipantDao participantDao;
    private final MatchDao matchDao;

    public TournamentServiceImpl(TournamentDao tournamentDao, ImageDao imageDao, ParticipantDao participantDao, MatchDao matchDao) {
        this.tournamentDao = tournamentDao;
        this.imageDao = imageDao;
        this.participantDao = participantDao;
        this.matchDao = matchDao;
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
    public List<Tournament> findByCreator(Long creator_id) {
        return tournamentDao.findByCreator(creator_id);
    }

    @Override
    public void setFinished(Long tournament_id, Long lastMatchId) {
        Tournament t = findById(tournament_id).orElse(null);
        Long winner;
        if (t != null) {
            if (t.getStructure() == Structure.LEAGUE) {
                List<ParticipantUser> tops = getLeagueTournamentTopPositions(tournament_id);
                if (tops.size() > 1) {
                    createMatchesLeague(t, tops, lastMatchId + 1, matchDao.getTournamentMaxStage(tournament_id) + 1, null);
                    return;
                }
                winner = tops.getFirst().getUser_id();
            } else {
                winner = matchDao.getMatchWinner(tournament_id, lastMatchId);
            }
            tournamentDao.setTournamentWinner(tournament_id, winner);
            tournamentDao.setFinished(tournament_id);
        }
    }

    private List<ParticipantUser> getLeagueTournamentTopPositions(Long tournamentId){
        Integer maxPoints = participantDao.getTournamentMaxPoints(tournamentId);
        if (maxPoints == null) return java.util.Collections.emptyList();
        return participantDao.getTournamentParticipantsByPoints(tournamentId, null, maxPoints);
    }

    @Override
    public void closeInscriptions(Long tournament_id){
        tournamentDao.closeInscriptions(tournament_id);
        createMatches(tournament_id, participantDao.getTournamentParticipantUsers(tournament_id));
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
        Tournament t = findById(tournament_id).orElse(null);
        if(t != null && t.getStructure().equals(Structure.HYBRID)){
            createGroupStageMatches(t);
        }
    }

    @Override
    public Map<Long,List<Tournament>> getUnfilteredTournamentPages(Long page) {
        return tournamentDao.getUnfilteredTournamentPages(page);
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

    private void createMatches(Long tournamentId, List<ParticipantUser> participants) {
        Tournament t = findById(tournamentId).orElse(null);

        if (t != null && !participants.isEmpty()) {
            if(t.getStructure().equals(Structure.ELIMINATION)) {
                createMatchesBracket(t, participants, 1L, null);
            } else if (t.getStructure().equals(Structure.HYBRID)) {
                createMatchesHybrid(t, participants);
            } else {
                createMatchesLeague(t, participants);
            }
        }
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants) {
        createMatchesLeague(t, participants, 1L, 1, null);
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants, Long firstMatchId, Integer firstStage, Boolean isGroupStage) {
        int n = participants.size();

        // Odd # of participants -> add fictional participant
        if (n % 2 != 0) {
            participants.add(null);
            n++;
        }
        int totalRounds = n - 1;
        Long matchId = firstMatchId;

        List<ParticipantUser> rotated = new ArrayList<>(participants);

        for (int round = firstStage; round < firstStage + totalRounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                ParticipantUser home = rotated.get(i);
                ParticipantUser away = rotated.get(n - 1 - i);

                if (home != null && away != null) {
                    matchDao.insertMatch(matchId++, t.getId(), home.getUser_id(), away.getUser_id(), round,null, null, null, isGroupStage);
                }
            }
            ParticipantUser first = rotated.remove(1);
            rotated.add(first);
        }
    }

    private void createMatchesBracket(Tournament t, List<ParticipantUser> participants, Long firstMatchId, Boolean isGroupStage) {
        int n = participants.size();

        int floorPowerOfTwo = 1;
        while (floorPowerOfTwo * 2 <= n) {
            floorPowerOfTwo *= 2;
        }
        int extras = n - floorPowerOfTwo;

        Long matchId = firstMatchId;
        Integer stage = matchDao.getTournamentMaxStage(t.getId()) + 1;

        List<ParticipantUser> nextRound = new ArrayList<>();

        for (int i = 0; i < extras; i++) {
            ParticipantUser home = participants.get(i * 2);
            ParticipantUser away = participants.get(i * 2 + 1);
            Long homeId = (home != null) ? home.getUser_id() : null;
            Long awayId = (away != null) ? away.getUser_id() : null;

            matchDao.insertMatch(matchId++, t.getId(), homeId, awayId, stage, null,null, null, isGroupStage);
            nextRound.add(null);
        }

        for (int i = extras * 2; i < n; i++) {
            nextRound.add(participants.get(i));
        }

        stage++;

        while (nextRound.size() > 1) {
            List<ParticipantUser> currentRound = nextRound;
            nextRound = new ArrayList<>();

            for (int i = 0; i < currentRound.size(); i += 2) {
                ParticipantUser home = currentRound.get(i);
                ParticipantUser away = (i + 1 < currentRound.size()) ? currentRound.get(i + 1) : null;
                Long homeId = (home != null) ? home.getUser_id() : null;
                Long awayId = (away != null) ? away.getUser_id() : null;
                matchDao.insertMatch(matchId++, t.getId(), homeId, awayId, stage,null, null, null, isGroupStage);
                nextRound.add(null);
            }

            stage++;
        }
    }

    private void createMatchesHybrid(Tournament t, List<ParticipantUser> participants) {
        int n = participants.size();
        if (n > 8){
            tournamentDao.setIsGroupStage(t.getId(), true);
            int groupsCount = calculateGroups(n);
            List<Integer> distribution = distributeParticipants(n);
            int index = 0;
            for (int g = 0; g < groupsCount; g++) {
                int size = distribution.get(g);
                List<ParticipantUser> group = new ArrayList<>(participants.subList(index, index + size));
                Long[] ids = group.stream().map(ParticipantUser::getUser_id).toArray(Long[]::new);
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

    @Override
    public void createBracketFromGroups(Long tournamentId) {
        Integer groups = participantDao.getTournamentGroups(tournamentId);
        Tournament t = findById(tournamentId).orElse(null);
        List<ParticipantUser> classified = new ArrayList<>(groups * 2);
        for(int i = 1; i <= groups; i++){
            Map<Integer, List<ParticipantUser>> topPositions = getGroupTopPositions(tournamentId, i);
            if(topPositions.get(1).size() > 1){
                createMatchesLeague(t, topPositions.get(1), 1L, matchDao.getTournamentGroupMaxStage(tournamentId, i), true);
                return;
            }else if(topPositions.get(2).size() > 1){
                // making sure the first remains at top
                participantDao.sumPoints(tournamentId, topPositions.get(1).getFirst().getUser_id(), 3 * (topPositions.get(2).size() / 2));

                createMatchesLeague(t, topPositions.get(2), 1L, matchDao.getTournamentGroupMaxStage(tournamentId, i), true);
                return;
            }
            ParticipantUser local   = topPositions.get(1).getFirst();
            ParticipantUser visitor = topPositions.get(2).getFirst();
            classified.add(local);
            classified.add(visitor);
        }

        tournamentDao.setIsGroupStage(tournamentId, false);
        createMatchesBracket(findById(tournamentId).orElse(null), classified, matchDao.getMaxMatchId(tournamentId) + 1, false);
    }

    private Map<Integer, List<ParticipantUser>> getGroupTopPositions(Long tournament_id, Integer group_number){
        Map<Integer, List<ParticipantUser>> out = new HashMap<>();
        out.put(1, participantDao.getTournamentParticipantsByPoints(tournament_id, group_number, participantDao.getTournamentMaxPoints(tournament_id)));
        out.put(2, participantDao.getTournamentParticipantsByPoints(tournament_id, group_number, participantDao.getTournamentSecondMaxPoints(tournament_id)));
        return out;
    }

    private void createGroupStageMatches(Tournament t){
        Map<Integer, List<ParticipantUser>> groupedParticipants = getGroupedParticipants(participantDao.getTournamentParticipantUsers(t.getId()));
        if(groupedParticipants != null){
            long nextId = 1L;
            for(List<ParticipantUser> participants : groupedParticipants.values()){
                createMatchesLeague(t, participants, nextId, 1, true);
                nextId += ((long) participants.size() * (participants.size() - 1)) / 2;
            }
        }
    }

    private Map<Integer, List<ParticipantUser>> getGroupedParticipants(List<ParticipantUser> participantUsers) {
        Map<Integer, List<ParticipantUser>> grouped = new TreeMap<>(Integer::compareTo);
        for (ParticipantUser p : participantUsers) {
            if (p.getGroupNumber() == null) {
                return null;
            }
            grouped.computeIfAbsent(p.getGroupNumber(), k -> new ArrayList<>()).add(p);
        }
        return grouped;
    }
}
