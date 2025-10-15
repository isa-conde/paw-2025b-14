package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.MatchWinnerAlreadySetException;
import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.MatchService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final MatchDao matchDao;
    private final ParticipantDao participantDao;
    private final TournamentDao tournamentDao;
    private final GameDao gameDao;
    private final TournamentService ts;
    private final UserService us;

    public MatchServiceImpl(MatchDao matchDao, ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService tournamentService, UserService userService, GameDao gameDao) {
        this.matchDao = matchDao;
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = tournamentService;
        this.gameDao = gameDao;
        this.us = userService;
    }

    @Transactional
    @Override
    public void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2){
        Match m1 = matchDao.getMatch(tournament_id, match1);
        Match m2 = matchDao.getMatch(tournament_id, match2);
        if (m1 == null || m2 == null) {
            throw new IllegalArgumentException("Both matches must exist in the tournament"); // TODO: custom handling
        }

        boolean u1IsLocalM1 = m1.getLocalId() != null && m1.getLocalId().equals(user1);
        boolean u1IsVisitM1 = m1.getVisitorId() != null && m1.getVisitorId().equals(user1);
        boolean u2IsLocalM2 = m2.getLocalId() != null && m2.getLocalId().equals(user2);
        boolean u2IsVisitM2 = m2.getVisitorId() != null && m2.getVisitorId().equals(user2);

        if ((!u1IsLocalM1 && !u1IsVisitM1) || (!u2IsLocalM2 && !u2IsVisitM2)) {
            throw new IllegalArgumentException("user1 must be in match1 and user2 must be in match2"); // TODO: custom handling
        }
        if (u1IsLocalM1) {
            matchDao.updateMatchLocal(tournament_id, match1, user2);
        } else {
            matchDao.updateMatchVisitor(tournament_id, match1, user2);
        }
        if (u2IsLocalM2) {
            matchDao.updateMatchLocal(tournament_id, match2, user1);
        } else {
            matchDao.updateMatchVisitor(tournament_id, match2, user1);
        }
        String username1 = us.findById(user1).get().getUsername();
        String username2 = us.findById(user2).get().getUsername();
        LOGGER.info("User {} and {} have been successfully swapped matches", username1, username2);
    }

    @Transactional
    @Override
    public Map<Integer, List<Match>> getTournamentMatchesByStage(Long tournamentId){
        List<Match> matches = matchDao.getTournamentMatches(tournamentId, ts.getPlayersPerTeam(tournamentId));
        if (matches.isEmpty()) {
            return Collections.emptyMap();
        }
        Integer teamSize = ts.getPlayersPerTeam(tournamentId);
        if(teamSize == null){
            teamSize = 1;
        }
        Map<Integer, List<Match>> result = new TreeMap<>();
        Boolean isGroupStage = tournamentDao.getIsGroupStage(tournamentId);
        for (Match m : matches) {
            m.setLocal(participantDao.getTournamentParticipantById(tournamentId, m.getLocalId(), teamSize));
            m.setVisitor(participantDao.getTournamentParticipantById(tournamentId, m.getVisitorId(), teamSize));
            Integer stage = m.getStage();
            if (stage == null || (m.getIsGroupStage() != null && isGroupStage != null && m.getIsGroupStage() != isGroupStage)) {
                continue;
            }
            result.computeIfAbsent(stage, s -> new ArrayList<>()).add(m);
        }
        return result;
    }

    @Transactional
    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        if (winner == null || (winner != 1 && winner != 2)) {
            throw new IllegalArgumentException("winner must be 1 (local) or 2 (visitor)"); // TODO: custom handling
        }
        if(hasWinner(matchId, tournamentId)) {
            LOGGER.warn("Match with ID {} already has a winner", matchId);
            throw new MatchWinnerAlreadySetException();
        }
        matchDao.setMatchWinner(matchId, tournamentId, winner);
        Match match = matchDao.getMatch(tournamentId, matchId);

        Long localId = match.getLocalId();
        Long visitorId = match.getVisitorId();
        if (localId == null || visitorId == null) {
            throw new IllegalStateException("Cannot set winner for TBD matches"); // TODO: custom handling
        }
        Long winnerId = (winner == 1) ? localId : visitorId;

        boolean isFinished = matchDao.allMatchesPlayed(tournamentId);

        Tournament t = ts.findById(tournamentId).orElse(null);
        if (t == null) {
            return;
        }
        Structure structure = t.getStructure();
        boolean isGroupStage = Boolean.TRUE.equals(t.getIs_group_stage());

        if(!isFinished && (structure.equals(Structure.ELIMINATION) || ( structure.equals(Structure.HYBRID) && !isGroupStage))) {
            setNextMatchInfo(matchId, tournamentId, winnerId);
        }else if(structure.equals(Structure.LEAGUE) || ( structure.equals(Structure.HYBRID) && isGroupStage)){
            participantDao.sumPoints(tournamentId, winnerId, 3, ts.getPlayersPerTeam(tournamentId));
        }
        if (structure.equals(Structure.HYBRID) && isGroupStage && isFinished) {
            ts.createBracketFromGroups(tournamentId, matchId);
            isFinished = matchDao.allMatchesPlayed(tournamentId);
        }
        if (isFinished) {
            ts.setFinished(tournamentId, matchId);
        }
        LOGGER.info("The winner of match with ID {} has been correctly set", matchId);
    }

    private void setNextMatchInfo(Long matchId, Long tournamentId, Long winnerId) {
        Integer currentStage = matchDao.getMatchStage(tournamentId, matchId);
        List<Long> idsThisStage = matchDao.getStageMatchIds(currentStage, tournamentId);
        int indexInStage = idsThisStage.indexOf(matchId);

        List<Long> idsNextStage = matchDao.getStageMatchIds(currentStage + 1, tournamentId);
        if(idsNextStage.isEmpty()){
            return;
        }
        Long parentMatchId = idsNextStage.get(indexInStage / 2);
        boolean isLeftChild = (indexInStage % 2 == 0);
        if (isLeftChild) {
            matchDao.updateMatchLocal(tournamentId, parentMatchId, winnerId);
        } else {
            matchDao.updateMatchVisitor(tournamentId, parentMatchId, winnerId);
        }
    }

    private boolean hasWinner(Long matchId, Long tournamentId) {
        Match match = matchDao.getMatch(tournamentId, matchId);
        return match.getWinner() != null;
    }
}
