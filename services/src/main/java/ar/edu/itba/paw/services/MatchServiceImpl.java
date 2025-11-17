package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.MatchService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Transactional(readOnly = true)
@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final MatchDao matchDao;
    private final ParticipantDao participantDao;
    private final TournamentDao tournamentDao;
    private final TournamentService ts;

    public MatchServiceImpl(MatchDao matchDao, ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService tournamentService) {
        this.matchDao = matchDao;
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = tournamentService;
    }

    @Transactional
    @Override
    public void swapMatchesMembers(long tournamentId, long match1, long match2, long user1, long user2){
        Match m1 = matchDao.getMatch(tournamentId, match1);
        Match m2 = matchDao.getMatch(tournamentId, match2);
        if (m1 == null || m2 == null) {
            throw new MatchNotFoundException();
        }

        boolean u1IsLocalM1 = m1.getLocalId() != null && m1.getLocalId().equals(user1);
        boolean u1IsVisitM1 = m1.getVisitorId() != null && m1.getVisitorId().equals(user1);
        boolean u2IsLocalM2 = m2.getLocalId() != null && m2.getLocalId().equals(user2);
        boolean u2IsVisitM2 = m2.getVisitorId() != null && m2.getVisitorId().equals(user2);

        if ((!u1IsLocalM1 && !u1IsVisitM1) || (!u2IsLocalM2 && !u2IsVisitM2)) {
            throw new ParticipantNotInMatchException();
        }
        if (u1IsLocalM1) {
            matchDao.updateMatchLocal(tournamentId, match1, user2);
        } else {
            matchDao.updateMatchVisitor(tournamentId, match1, user2);
        }
        if (u2IsLocalM2) {
            matchDao.updateMatchLocal(tournamentId, match2, user1);
        } else {
            matchDao.updateMatchVisitor(tournamentId, match2, user1);
        }
        LOGGER.info("User {} and {} have been successfully swapped matches", user1, user2);
    }

    @Transactional
    @Override
    public Map<Integer, List<Match>> getTournamentMatchesByStage(long tournamentId){
        int teamSize = ts.getPlayersPerTeam(tournamentId);
        List<Match> matches = matchDao.getTournamentMatches(tournamentId, teamSize);
        if (matches.isEmpty()) {
            return Collections.emptyMap();
        }
        matches.sort(Comparator.comparingLong(Match::getId));
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
    public void setMatchResults(long matchId, long tournamentId, Integer localScore, Integer visitorScore) {
        if (localScore == null || visitorScore == null) {
            throw new ScoresInvalidException();
        }
        if(hasWinner(matchId, tournamentId)) {
            LOGGER.warn("Match with ID {} already has a winner", matchId);
            throw new MatchWinnerAlreadySetException();
        }

        Tournament tournament = ts.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Structure structure = tournament.getStructure();
        boolean isGroupStage = Boolean.TRUE.equals(tournament.getIsGroupStage());
        boolean isElimination = structure.equals(Structure.ELIMINATION) || ( structure.equals(Structure.HYBRID) && !isGroupStage);

        if(isElimination && localScore.equals(visitorScore)){
            throw new IllegalArgumentException("Cannot draw in Elimination match"); // TODO: custom handling
        }
        Match match = matchDao.getMatch(tournamentId, matchId);
        Long localId = match.getLocalId();
        Long visitorId = match.getVisitorId();
        if (localId == null || visitorId == null) {
            throw new IllegalStateException("Cannot set winner for TBD matches"); // TODO: custom handling
        }

        int winner = (localScore > visitorScore) ? 1 : (localScore < visitorScore ? 2 : -1);
        matchDao.setMatchResults(matchId, tournamentId, localScore, visitorScore, winner, LocalDate.now());
        Long winnerId = (winner == 1) ? localId : (winner == 2 ? visitorId : null);
        int scoreDifference = (winner == 1) ? localScore - visitorScore : (winner == 2 ? visitorScore - localScore : 0);

        boolean isFinished = matchDao.allMatchesPlayed(tournamentId);
        if(!isFinished && isElimination) {
            setNextMatchInfo(matchId, tournamentId, winnerId);
        }else if(structure.equals(Structure.LEAGUE) || ( structure.equals(Structure.HYBRID) && isGroupStage)){
            if(winner == -1) {
                participantDao.sumPoints(tournamentId, localId, 1, scoreDifference, ts.getPlayersPerTeam(tournamentId));
                participantDao.sumPoints(tournamentId, visitorId, 1, scoreDifference, ts.getPlayersPerTeam(tournamentId));
            }else {
                participantDao.sumPoints(tournamentId, winnerId, 3, scoreDifference, ts.getPlayersPerTeam(tournamentId));
            }
        }
        if (structure.equals(Structure.HYBRID) && isGroupStage && isFinished) {
            ts.createBracketFromGroups(tournamentId);
            isFinished = matchDao.allMatchesPlayed(tournamentId);
        }
        if (isFinished) {
            ts.setFinished(tournamentId, matchId);
        }
        LOGGER.info("The winner of match with ID {} has been correctly set", matchId);
    }

    private void setNextMatchInfo(long matchId, long tournamentId, Long winnerId) {
        Integer currentStage = matchDao.getMatchStage(tournamentId, matchId);
        if(currentStage == null) {
            throw new StageIsNotSetException();
        }

        List<Long> idsThisStage = matchDao.getStageMatchIds(currentStage, tournamentId).stream().sorted().toList();
        List<Long> idsNextStage = matchDao.getStageMatchIds(currentStage + 1, tournamentId).stream().sorted().toList();

        int indexInStage = idsThisStage.indexOf(matchId);
        if(idsNextStage.isEmpty()){
            return;
        }
        Long parentMatchId = idsNextStage.get(indexInStage / 2);
        if(parentMatchId == null) {
            throw new MatchNotFoundException();
        }
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
