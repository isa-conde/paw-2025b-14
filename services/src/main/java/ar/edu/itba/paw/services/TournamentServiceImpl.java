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
    public Optional<Structure> getTournamentStructure(Long tournament_id){
    	return Optional.ofNullable(tournamentDao.getTournamentStructure(tournament_id));
    }

    @Override
    public Map<Integer, Map<Integer, List<MatchWithPlayers>>> getTournamentMatchesByGroup(Long tournamentId) {
        List<MatchWithPlayers> matches = matchDao.getTournamentMatches(tournamentId);
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
        return tournamentDao.findByCreator(creator_id);
    }

    @Override
    public void setFinished(Long tournament_id, Long lastMatchId) {
        Tournament t = findById(tournament_id).orElse(null);
        Long winner;
        if (t != null) {
            if (t.getStructure() == Structure.LEAGUE) {
                List<ParticipantUser> tops = tournamentDao.getLeagueTournamentTopPositions(tournament_id);
                if (tops.size() > 1) {
                    //TODO - new matches
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

    @Override
    public void closeInscriptions(Long tournament_id){
        tournamentDao.closeInscriptions(tournament_id);
        createMatches(tournament_id, participantDao.getTournamentParticipantUsers(tournament_id));
    }

    public void createMatches(Long tournamentId, List<ParticipantUser> participants) {
        Tournament t = findById(tournamentId).orElse(null);

        if (t != null && !participants.isEmpty()) {
            if(t.getStructure().equals(Structure.ELIMINATION)) {
                createMatchesBracket(t, participants, 1L);
            } else if (t.getStructure().equals(Structure.HYBRID)) {
                createMatchesHybrid(t, participants);
            } else {
                createMatchesLeague(t, participants);
            }
        }
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants) {
        createMatchesLeague(t, participants, 1L);
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants, Long firstMatchId) {
        int n = participants.size();

        // Odd # of participants -> add fictional participant
        if (n % 2 != 0) {
            participants.add(null);
            n++;
        }
        int totalRounds = n - 1;
        Long matchId = firstMatchId;

        List<ParticipantUser> rotated = new ArrayList<>(participants);

        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                ParticipantUser home = rotated.get(i);
                ParticipantUser away = rotated.get(n - 1 - i);

                if (home != null && away != null) {
                    matchDao.insertMatch(matchId++, t.getId(), home.getUser_id(), away.getUser_id(), null, null, null, round);
                }
            }
            ParticipantUser first = rotated.remove(1);
            rotated.add(first);
        }
    }

    public void createMatchesBracket(Tournament t, List<ParticipantUser> participants, Long firstMatchId) {
        int n = participants.size();

        int floorPowerOfTwo = 1;
        while (floorPowerOfTwo * 2 <= n) {
            floorPowerOfTwo *= 2;
        }
        int extras = n - floorPowerOfTwo;

        Long matchId = firstMatchId;
        int stage = 1;

        List<ParticipantUser> nextRound = new ArrayList<>();

        for (int i = 0; i < extras; i++) {
            ParticipantUser home = participants.get(i * 2);
            ParticipantUser away = participants.get(i * 2 + 1);

            matchDao.insertMatch(matchId++, t.getId(), home.getUser_id(), away.getUser_id(), null, null, null, stage);
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
                matchDao.insertMatch(matchId++, t.getId(), home.getUser_id(), away.getUser_id(), null, null, null, stage);
                nextRound.add(null);
            }

            stage++;
        }
    }

    public void createMatchesHybrid(Tournament t, List<ParticipantUser> participants) {
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
            createMatchesBracket(t, participants, 1L);
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

    private void createBracketFromGroups(Long tournamentId) {
        final String sql = """
            WITH ranked AS (
              SELECT pu.user_id,
                     pu.group_number,
                     ROW_NUMBER() OVER (
                       PARTITION BY pu.group_number
                       ORDER BY pu.points DESC, pu.user_id
                     ) AS rk
              FROM participant_user pu
              WHERE pu.tournament_id = ? AND pu.group_number IS NOT NULL
            )
            SELECT group_number,
                   MAX(CASE WHEN rk = 1 THEN user_id END) AS first_id,
                   MAX(CASE WHEN rk = 2 THEN user_id END) AS second_id
            FROM ranked
            GROUP BY group_number
            ORDER BY group_number
        """;

        List<Map<String, Object>> pairs = jdbcTemplate.queryForList(sql, tournamentId);
        int g = pairs.size();
        if (g == 0) {
            return;
        }

        List<ParticipantUser> classified = new ArrayList<>(g * 2);
        for (int i = 0; i < g; i++) {
            Long firstId  = ((Number) pairs.get(i).get("first_id")).longValue();
            Long secondId = ((Number) pairs.get((i + 1) % g).get("second_id")).longValue();

            ParticipantUser local   = getTournamentParticipantByUserId(tournamentId, firstId);
            ParticipantUser visitor = getTournamentParticipantByUserId(tournamentId, secondId);

            classified.add(local);
            classified.add(visitor);
        }

        Long maxId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id), 0) FROM match WHERE tournament_id = ?",
                Long.class, tournamentId
        );

        jdbcTemplate.update("UPDATE tournament SET is_group_stage = false WHERE id = ?", t.getId());
        createMatchesBracket(t, classified, maxId + 1);
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
}
