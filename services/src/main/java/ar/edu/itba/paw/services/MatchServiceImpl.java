package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.MatchDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.MatchService;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService {

    private final TournamentDao tournamentDao;
    private final ParticipantDao participantDao;
    private final MatchDao matchDao;

    public MatchServiceImpl(TournamentDao tournamentDao, ParticipantDao participantDao, MatchDao matchDao) {
        this.tournamentDao = tournamentDao;
        this.participantDao = participantDao;
        this.matchDao = matchDao;
    }

    @Override
    public void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2){
        matchDao.swapMatchesMembers(tournament_id, match1, match2, user1, user2);
    }

    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        matchDao.setMatchWinner(matchId, tournamentId, winner);
    }
}
