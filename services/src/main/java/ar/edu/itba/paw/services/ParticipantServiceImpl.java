package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.UserAlreadyJoinedException;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.ParticipantInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final ParticipantDao participantDao;
    private final TournamentDao tournamentDao;
    private final TournamentService ts;

    public ParticipantServiceImpl(ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService ts){
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = ts;
    }

    @Transactional
    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        if(hasJoined(user_id, tournament_id)) {
            LOGGER.warn("User with ID {} has already joined tournament with ID {}", user_id, tournament_id);
            throw new UserAlreadyJoinedException();
        }

        participantDao.joinTournamentUser(user_id, tournament_id);

        List<ParticipantUser> participantUsers = getTournamentParticipantUsers(tournament_id);
        Optional<Tournament> tournament = tournamentDao.findById(tournament_id);
        if (tournament.isPresent() && participantUsers.size() == tournament.get().getMax_participants()) {
            ts.closeInscriptions(tournament_id);
            LOGGER.info("Max participant count has been reached. The inscriptions for tournament with ID {} have been closed", tournament_id);
        }
        LOGGER.info("User with ID {} has joined tournament with ID {}", user_id, tournament_id);
    }

    @Override
    public List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id) {
        return participantDao.getTournamentParticipantUsers(tournament_id);
    }

    @Override
    public List<ParticipantInfo> getTournamentParticipantInfo(Long tournamentId, Integer teamSize) {
        List<ParticipantInfo> participants = new ArrayList<>();
        if (teamSize > 1){
            participants = participantDao.getTournamentsParticipantTeamsInfo(tournamentId);
        }else {
            participants = participantDao.getTournamentsParticipantUsersInfo(tournamentId);
        }
        participants.sort((a, b) -> b.getPoints().compareTo(a.getPoints()));
        return participants;
    }

    @Override
    public ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
        return participantDao.getTournamentParticipantByUserId(tournament_id, user_id);
    }

    @Override
    public Integer getTournamentGroups(Long tournamentId){
        return participantDao.getTournamentGroups(tournamentId);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        return participantDao.hasJoined(userId, tournamentId);
    }

    @Transactional
    @Override
    public void leaveTournamentUser(Long user_id, Long tournament_id) {
        participantDao.leaveTournamentUser(user_id, tournament_id);
        LOGGER.info("User with ID {} has successfully left tournament with ID {}", user_id, tournament_id);
    }

    @Transactional
    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2){
        if (tournamentDao.isTournamentStarted(tournament_id)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started"); // TODO: custom handling
        }

        Integer g1 = participantDao.getGroupNumber(tournament_id, user1);
        Integer g2 = participantDao.getGroupNumber(tournament_id, user2);

        if (g1.equals(g2)) {
            LOGGER.warn("Cannot swap users within the same group");
            return;
        }
        participantDao.swapGroups(tournament_id, user1, user2, g1, g2);
        LOGGER.info("Users with IDs {} and {} have successfully swapped groups", user1, user2);
    }
}
