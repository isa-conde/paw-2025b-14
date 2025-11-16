package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.model.Match.Match;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.ids.MatchId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Repository
public class MatchHibernateDao implements MatchDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner, Boolean isGroupStage) {
        MatchId matchId = new MatchId(id, tournamentId);
        Match match = new Match(matchId, localScore, visitorScore, winner, stage, isGroupStage);
        match.setTournament(em.getReference(Tournament.class, matchId.getTournamentId()));
        if (localId != null) {
            match.setLocal(em.getReference(Participant.class, localId));
        } else {
            match.setLocal(null);
        }
        if (visitorId != null) {
            match.setVisitor(em.getReference(Participant.class, visitorId));
        } else {
            match.setVisitor(null);
        }
        em.persist(match);
    }

    @Override
    public Long getMatchWinner(Long tournamentId, Long matchId) {
        em.flush();
        MatchId id = new MatchId(matchId, tournamentId);
        Match match = em.find(Match.class, id);
        if(match == null) {
            LOGGER.warn("Match not found for tournamentId={} matchId={}", tournamentId, matchId);
            return null; //TODO: add custom excep
        }
        Integer winner = match.getWinner();
        if(winner == null) {
            LOGGER.warn("Match does not have a winner");
            return null; // TODO: add custom excep
        }
        if(winner == 1) {
            return match.getLocalId();
        }else if(winner == 2) {
            return match.getVisitorId();
        }
        return null;
    }

    @Override
    public void setMatchResults(Long matchId, Long tournamentId, Integer localScore, Integer visitorScore, Integer winner, LocalDate date) {
        MatchId id = new MatchId(matchId, tournamentId);
        Match match = em.find(Match.class, id);
        if (match == null) {
            LOGGER.warn("Match not found for tournamentId={} matchId={}", tournamentId, matchId);
            return;
        }
        match.setLocalScore(localScore);
        match.setVisitorScore(visitorScore);
        match.setWinner(winner);
        match.setDate(date);
    }

    @Override
    public List<Match> getTournamentMatches(Long tournamentId, Integer teamSize) {
        Tournament tournament = em.find(Tournament.class, tournamentId);
        return tournament.getMatches();
    }

    @Override
    public Match getMatch(long tournamentId, long matchId) {
        MatchId id = new MatchId(matchId, tournamentId);
        return em.find(Match.class, id);
    }

    @Override
    public Long getMaxMatchId(Long tournamentId) {
        TypedQuery<Long> query = em.createQuery("SELECT MAX(m.id.id) FROM Match m WHERE m.tournament = :tournament", Long.class);
        query.setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        return query.getSingleResult();
    }

    @Override
    public void updateMatchLocal(Long tournamentId, Long matchId, Long userId) {
        MatchId id = new MatchId(matchId, tournamentId);
        Query query = em.createQuery("UPDATE Match m SET m.local = :newLocal WHERE m.id = :id");
        Participant newLocal = em.find(Participant.class, userId);
        query.setParameter("newLocal", newLocal).setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public void updateMatchVisitor(Long tournamentId, Long matchId, Long userId) {
        MatchId id = new MatchId(matchId, tournamentId);
        Query query = em.createQuery("UPDATE Match m SET m.visitor = :newVisitor WHERE m.id = :id");
        Participant newVisitor = em.find(Participant.class, userId);
        query.setParameter("newVisitor", newVisitor).setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public Boolean allMatchesPlayed(Long tournamentId) {
        TypedQuery<Long> totalMatchesCountQuery = em.createQuery("SELECT COUNT(m) FROM Match m WHERE m.tournament = :tournament", Long.class);
        totalMatchesCountQuery.setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        Long totalMatches = totalMatchesCountQuery.getSingleResult();

        if(totalMatches == 0) return false;

        TypedQuery<Long> playedMatchesCountQuery = em.createQuery("SELECT COUNT(m) FROM Match m WHERE m.tournament = :tournament AND m.winner IS NOT NULL", Long.class);
        playedMatchesCountQuery.setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        Long playedMatches = playedMatchesCountQuery.getSingleResult();

        return totalMatches.equals(playedMatches);
    }

    @Override
    public Integer getMatchStage(Long tournamentId, Long matchId) {
        MatchId id = new MatchId(matchId, tournamentId);
        TypedQuery<Integer> query = em.createQuery("SELECT m.stage FROM Match m WHERE m.id = :id", Integer.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public List<Long> getStageMatchIds(Integer stage, Long tournamentId) {
        TypedQuery<Long> query = em.createQuery("SELECT m.id.id FROM Match m WHERE m.stage = :stage AND m.tournament = :tournament", Long.class);
        query.setParameter("stage", stage).setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        return query.getResultList();
    }

    @Override
    public Integer getTournamentMaxStage(Long tournamentId) {
        TypedQuery<Integer> query = em.createQuery("SELECT COALESCE(MAX(m.stage), 0) FROM Match m WHERE m.tournament = :tournament", Integer.class);
        query.setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        return query.getSingleResult();
    }

    @Override
    public Integer getTournamentGroupMaxStage(Long tournamentId, Integer groupNumber) {
        TypedQuery<Integer> query = em.createQuery("SELECT COALESCE(MAX(m.stage), 0) FROM Match m WHERE m.tournament = :tournament AND m.local.groupNumber = :groupNumber", Integer.class);
        query.setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        query.setParameter("groupNumber", groupNumber);
        return query.getSingleResult();
    }
}
