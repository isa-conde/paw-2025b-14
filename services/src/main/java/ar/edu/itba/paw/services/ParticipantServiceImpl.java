package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.UserAlreadyJoinedException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final static int SINGLE_USER = 1;

    private final ParticipantDao participantDao;
    private final TournamentDao tournamentDao;
    private final TournamentService ts;
    private final UserDao userDao;
    private final TeamDao teamDao;
    private final MailService ms;
    private final GameFormatDao gameFormatDao;

    public ParticipantServiceImpl(ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService ts, GameFormatDao gameFormatDao, UserDao userDao, TeamDao teamDao, MailService ms){
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = ts;
        this.userDao = userDao;
        this.teamDao = teamDao;
        this.ms = ms;
        this.gameFormatDao = gameFormatDao;
    }

    @Transactional
    @Override
    public void joinTournamentUser(Long userId, Long tournamentId) {
        if(hasJoined(userId, tournamentId)) {
            LOGGER.warn("User with ID {} has already joined tournament with ID {}", userId, tournamentId);
            throw new UserAlreadyJoinedException();
        }

        participantDao.joinTournamentUser(userId, tournamentId);

        List<Participant> participants = getTournamentParticipantUsers(tournamentId);
        Optional<Tournament> tournament = tournamentDao.findById(tournamentId);
        if (tournament.isPresent() && participants.size() == tournament.get().getMaxParticipants()) {
            ts.closeInscriptions(tournamentId);
            LOGGER.info("Max participant count has been reached. The inscriptions for tournament with ID {} have been closed", tournamentId);
        }
        LOGGER.info("User with ID {} has joined tournament with ID {}", userId, tournamentId);
        User user = userDao.findById(userId).get();
        User creator = userDao.findById(tournament.get().getCreatorId()).get();
        ms.sendTournamentJoinedEmail(tournamentId, user.getUsername(), tournament.get().getName(), user.getEmail(), creator.getEmail());
        LOGGER.info("Tournament joined email correctly sent to the address {}", user.getEmail());
        ms.sendTournamentJoinedOwnerEmail(tournamentId, creator.getUsername(), user.getUsername(), tournament.get().getName(), creator.getEmail());
        LOGGER.info("Tournament joined notification email correctly sent to tournament owner with address {}", creator.getEmail());
    }

    private List<Participant> getTournamentParticipantUsers(Long tournamentId) {
        return participantDao.getTournamentParticipantUsers(tournamentId);
    }

    @Override
    public List<Participant> getTournamentParticipants(Long tournamentId, Integer teamSize) {
        List<Participant> participants;
        if (teamSize > 1){
            participants = participantDao.getTournamentParticipantTeams(tournamentId);
        }else {
            participants = participantDao.getTournamentParticipantUsers(tournamentId);
        }
        participants.sort(Comparator.comparingInt(Participant::getPoints).thenComparingInt(Participant::getScoreDifference));
        return participants.reversed();
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
    public void leaveTournament(Long userId, Long tournamentId) {
        Integer playersPerTeam = ts.getPlayersPerTeam(tournamentId);
        if(playersPerTeam != null){
            if(playersPerTeam > 1){
                participantDao.leaveTournamentTeam(userId, tournamentId);
            }else{
                participantDao.leaveTournamentUser(userId, tournamentId);
            }
        }
        LOGGER.info("User with ID {} has successfully left tournament with ID {}", userId, tournamentId);
    }

    @Transactional
    @Override
    public void swapGroups(Long tournamentId, Long user1, Long user2){
        if (tournamentDao.isTournamentStarted(tournamentId)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started"); // TODO: custom handling
        }
        Optional<Tournament> t = tournamentDao.findById(tournamentId);
        Integer teamSize = gameFormatDao.getFormatById(t.get().getFormatId()).get().getPlayersPerTeam();

        Integer g1 = participantDao.getGroupNumber(tournamentId, user1, teamSize);
        Integer g2 = participantDao.getGroupNumber(tournamentId, user2, teamSize);

        if (g1.equals(g2)) {
            LOGGER.warn("Cannot swap users within the same group");
            return;
        }

        participantDao.swapGroups(tournamentId, user1, user2, g1, g2, teamSize);
        LOGGER.info("Users with IDs {} and {} have successfully swapped groups", user1, user2);
    }

    @Transactional
    @Override
    public void joinTournamentTeam(Long tournamentId, Long teamId, List<Long> participants){
        Optional<Tournament> tournament = tournamentDao.findById(tournamentId);
        Optional<Team> optionalTeam = teamDao.getById(teamId);
        if(tournament.isEmpty() || optionalTeam.isEmpty()){
            return;
        }
        Tournament t = tournament.get();
        Team team = optionalTeam.get();
        User creator = userDao.findById(t.getCreatorId()).get();

        for(Long p : participants){
            if(hasJoined(p, tournamentId)) {
                LOGGER.warn("User with ID {} has already joined tournament with ID {}", p, tournamentId);
                throw new UserAlreadyJoinedException();
            }
        }
        List<Participant> currentParticipants = getTournamentParticipants(tournamentId, ts.getPlayersPerTeam(tournamentId));
        for(Long p : participants){
            participantDao.joinTournamentUserWithTeam(p, tournamentId, teamId);
            User user = userDao.findById(p).get();
            ms.sendTournamentJoinedEmail(tournamentId, user.getUsername(), t.getName(), user.getEmail(), creator.getEmail());
            LOGGER.info("Tournament joined email correctly sent to the address {}", user.getEmail());
        }
        ms.sendTournamentTeamJoinedOwnerEmail(tournamentId, creator.getUsername(), team.getName(), t.getName(), creator.getEmail());
        LOGGER.info("Tournament joined notification email correctly sent to tournament owner with address {}", creator.getEmail());
        participantDao.joinTournamentTeam(tournamentId, teamId);
        if (currentParticipants.size() + 1 == t.getMaxParticipants()) {
            ts.closeInscriptions(tournamentId);
            LOGGER.info("Max participant count has been reached. The inscriptions for tournament with ID {} have been closed", tournamentId);
        }
    }

    @Override
    public Boolean participantHasRatedTournament(Long tournamentId, Long userId) {
        return participantDao.hasRated(tournamentId, userId);
    }

    @Transactional
    @Override
    public void updateCreatorRating(Long tournamentId, Long creatorId, Long reviewerId, Float rating) {
        if(participantDao.hasRated(tournamentId, reviewerId)) {
            throw new UserNotFoundException();
        }
        participantDao.updateHasRated(reviewerId, tournamentId);
        User user = userDao.findById(creatorId).get();
        Float currentRating = user.getRating();
        Float newRating = (currentRating == null) ? rating : (currentRating + rating) / 2;
        userDao.updateUserRating(creatorId, newRating);
    }
}
