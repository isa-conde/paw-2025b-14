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
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        if(hasJoined(user_id, tournament_id)) {
            LOGGER.warn("User with ID {} has already joined tournament with ID {}", user_id, tournament_id);
            throw new UserAlreadyJoinedException();
        }

        participantDao.joinTournamentUser(user_id, tournament_id);

        List<Participant> participants = getTournamentParticipantUsers(tournament_id);
        Optional<Tournament> tournament = tournamentDao.findById(tournament_id);
        if (tournament.isPresent() && participants.size() == tournament.get().getMax_participants()) {
            ts.closeInscriptions(tournament_id);
            LOGGER.info("Max participant count has been reached. The inscriptions for tournament with ID {} have been closed", tournament_id);
        }
        LOGGER.info("User with ID {} has joined tournament with ID {}", user_id, tournament_id);
        User user = userDao.findById(user_id).get();
        User creator = userDao.findById(tournament.get().getCreator_id()).get();
        ms.sendTournamentJoinedEmail(tournament_id, user.getUsername(), tournament.get().getName(), user.getEmail(), creator.getEmail());
        LOGGER.info("Tournament joined email correctly sent to the address {}", user.getEmail());
        ms.sendTournamentJoinedOwnerEmail(tournament_id, creator.getUsername(), user.getUsername(), tournament.get().getName(), creator.getEmail());
        LOGGER.info("Tournament joined notification email correctly sent to tournament owner with address {}", creator.getEmail());
    }

    private List<Participant> getTournamentParticipantUsers(Long tournament_id) {
        return participantDao.getTournamentParticipantUsers(tournament_id);
    }

    @Override
    public List<Participant> getTournamentParticipants(Long tournamentId, Integer teamSize) {
        List<Participant> participants;
        if (teamSize > 1){
            participants = participantDao.getTournamentParticipantTeams(tournamentId);
        }else {
            participants = participantDao.getTournamentParticipantUsers(tournamentId);
        }
        participants.sort(Comparator.comparingInt(Participant::getPoints).thenComparingInt(Participant::getScore_difference));
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
    public void leaveTournament(Long user_id, Long tournament_id) {
        Integer playersPerTeam = ts.getPlayersPerTeam(tournament_id);
        if(playersPerTeam != null){
            if(playersPerTeam > 1){
                participantDao.leaveTournamentTeam(user_id, tournament_id);
            }else{
                participantDao.leaveTournamentUser(user_id, tournament_id);
            }
        }
        LOGGER.info("User with ID {} has successfully left tournament with ID {}", user_id, tournament_id);
    }

    @Transactional
    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2){
        if (tournamentDao.isTournamentStarted(tournament_id)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started"); // TODO: custom handling
        }
        Optional<Tournament> t = tournamentDao.findById(tournament_id);
        Integer teamSize = gameFormatDao.getFormatById(t.get().getFormat_id()).get().getPlayers_per_team();

        Integer g1 = participantDao.getGroupNumber(tournament_id, user1, teamSize);
        Integer g2 = participantDao.getGroupNumber(tournament_id, user2, teamSize);

        if (g1.equals(g2)) {
            LOGGER.warn("Cannot swap users within the same group");
            return;
        }

        participantDao.swapGroups(tournament_id, user1, user2, g1, g2, teamSize);
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
        User creator = userDao.findById(t.getCreator_id()).get();

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
        if (currentParticipants.size() + 1 == t.getMax_participants()) {
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
        userDao.updateUserRating(creatorId, rating);
    }
}
