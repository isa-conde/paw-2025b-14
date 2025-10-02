package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantUserInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    ParticipantDao participantDao;
    TournamentDao tournamentDao;
    TournamentService ts;

    public ParticipantServiceImpl(ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService ts){
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = ts;
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        participantDao.joinTournamentUser(user_id, tournament_id);

        List<ParticipantUser> participantUsers = getTournamentParticipantUsers(tournament_id);
        Optional<Tournament> tournament = tournamentDao.findById(tournament_id);
        if (tournament.isPresent() && participantUsers.size() == tournament.get().getMax_participants()) {
            ts.closeInscriptions(tournament_id);
        }
    }

    @Override
    public List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id) {
        return participantDao.getTournamentParticipantUsers(tournament_id);
    }

    @Override
    public List<ParticipantUserInfo> getTournamentParticipantUsersInfo(Long tournamentId) {
        List<ParticipantUserInfo> participants = participantDao.getTournamentsParticipantUsersInfo(tournamentId);
        participants.sort((a, b) -> b.getPoints().compareTo(a.getPoints()));
        return participants;
    }

    @Override
    public ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
        return participantDao.getTournamentParticipantByUserId(tournament_id, user_id);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        return participantDao.hasJoined(userId, tournamentId);
    }

    @Override
    public void leaveTournamentUser(Long user_id, Long tournament_id) {
        participantDao.leaveTournamentUser(user_id, tournament_id);
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2){
        if (tournamentDao.isTournamentStarted(tournament_id)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started");
        }

        Integer g1 = participantDao.getGroupNumber(tournament_id, user1);
        Integer g2 = participantDao.getGroupNumber(tournament_id, user2);

        if (g1.equals(g2)) {
            return;
        }
        participantDao.swapGroups(tournament_id, user1, user2, g1, g2);
    }
}
