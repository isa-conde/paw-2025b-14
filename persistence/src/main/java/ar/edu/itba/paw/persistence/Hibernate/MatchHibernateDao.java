package ar.edu.itba.paw.persistence.Hibernate;

import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.MatchInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.ids.MatchId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class MatchHibernateDao implements MatchDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public void insertMatch(Long id, Long tournamentId, Long localId, Long visitorId, Integer stage, Integer localScore, Integer visitorScore, Integer winner, Boolean isGroupStage) {
        MatchId matchId = new MatchId(id, tournamentId);
        Match match = new Match(matchId, localId, visitorId, localScore, visitorScore, winner, stage, isGroupStage);
        em.persist(match);
    }

    @Override
    public Long getMatchWinner(Long tournamentId, Long matchId) {
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
        return winner.longValue();
    }

    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        MatchId id = new MatchId(matchId, tournamentId);
        Query query = em.createQuery("UPDATE Match m SET m.winner = :winner WHERE m.id = :id");
        int updated = query.setParameter("winner", winner).setParameter("id", id).executeUpdate();

        if (updated == 0) {
            LOGGER.warn("Match not found for tournamentId={} matchId={}", tournamentId, matchId); // TODO: add custom excep
        }
    }

    @Override
    public List<MatchInfo> getTournamentMatches(Long tournament_id, Integer teamSize) {
        Tournament tournament = em.find(Tournament.class, tournament_id);
        return null;
    }

    @Override
    public Match getMatch(long tournamentId, long matchId) {
        MatchId id = new MatchId(matchId, tournamentId);
        return em.find(Match.class, id);
    }

    @Override
    public Long getMaxMatchId(Long tournamentId) {
        TypedQuery<Long> query = em.createQuery("SELECT MAX(m.id) FROM Match m WHERE m.tournament = :tournament", Long.class);
        query.setParameter("tournament", em.getReference(Tournament.class, tournamentId));
        return query.getSingleResult();
    }

    @Override
    public void updateMatchLocal(Long tournamentId, Long matchId, Long userId) { // TODO: will probably change with MatchInfo refactor
        MatchId id = new MatchId(matchId, tournamentId);
        Query query = em.createQuery("UPDATE Match m SET m.localId = :newLocal WHERE m.id = :id");
        query.setParameter("newLocal", userId).setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public void updateMatchVisitor(Long tournamentId, Long matchId, Long userId) {
        MatchId id = new MatchId(matchId, tournamentId);
        Query query = em.createQuery("UPDATE Match m SET m.visitorId = :newVisitor WHERE m.id = :id");
        query.setParameter("newVisitor", userId).setParameter("id", id);
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
        Long playedMatches = totalMatchesCountQuery.getSingleResult();

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
        TypedQuery<Long> query = em.createQuery("SELECT m.id FROM Match m WHERE m.stage = :stage AND m.tournament = :tournament", Long.class);
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
        return 0;
    }
}
