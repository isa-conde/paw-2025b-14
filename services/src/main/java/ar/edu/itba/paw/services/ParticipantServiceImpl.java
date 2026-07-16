package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.*;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.Game.GameFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final ParticipantDao participantDao;
    private final TournamentDao tournamentDao;
    private final TournamentService ts;
    private final UserDao userDao;
    private final TeamDao teamDao;
    private final MailService ms;

    public ParticipantServiceImpl(ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService ts, UserDao userDao, TeamDao teamDao, MailService ms){
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = ts;
        this.userDao = userDao;
        this.teamDao = teamDao;
        this.ms = ms;
    }

    @Transactional
    @Override
    public void joinTournamentUser(long userId, long tournamentId) {
        if(hasJoined(userId, tournamentId)) {
            LOGGER.warn("User with ID {} has already joined tournament with ID {}", userId, tournamentId);
            throw new UserAlreadyJoinedException();
        }

        participantDao.joinTournamentUser(userId, tournamentId);

        List<Participant> participants = getTournamentParticipantUsers(tournamentId);
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if (participants.size() == tournament.getMaxParticipants()) {
            ts.closeInscriptions(tournamentId);
            LOGGER.info("Max participant count has been reached. The inscriptions for tournament with ID {} have been closed", tournamentId);
        }
        LOGGER.info("User with ID {} has joined tournament with ID {}", userId, tournamentId);
        User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
        User creator = userDao.findById(tournament.getCreatorId()).orElseThrow(UserNotFoundException::new);
        ms.sendTournamentJoinedEmail(tournamentId, user.getUsername(), tournament.getName(), user.getEmail(), creator.getEmail());
        LOGGER.info("Tournament joined email correctly sent to the address {}", user.getEmail());
        ms.sendTournamentJoinedOwnerEmail(tournamentId, creator.getUsername(), user.getUsername(), tournament.getName(), creator.getEmail());
        LOGGER.info("Tournament joined notification email correctly sent to tournament owner with address {}", creator.getEmail());
    }

    private List<Participant> getTournamentParticipantUsers(long tournamentId) {
        return participantDao.getTournamentParticipantUsers(tournamentId);
    }

    @Override
    public List<Participant> getTournamentParticipants(long tournamentId, int teamSize) {
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
    public Participant getTournamentParticipant(long tournamentId, long participantId) {
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        GameFormat format = tournament.getFormatEntity();
        int teamSize = format == null ? 1 : format.getPlayersPerTeam();
        Participant participant = participantDao.getTournamentParticipantById(tournamentId, participantId, teamSize);
        if (participant == null) {
            throw new ParticipantNotFoundException();
        }
        return participant;
    }

    @Override
    public int getTournamentGroups(long tournamentId){
        return participantDao.getTournamentGroups(tournamentId);
    }

    @Override
    public boolean hasJoined(Long userId, long tournamentId) {
        if(userId == null) {
            throw new ParticipantNotFoundException();
        }
        return participantDao.hasJoined(userId, tournamentId);
    }

    @Transactional
    @Override
    public void leaveTournament(long userId, long tournamentId) {
        Tournament tournament = ts.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        GameFormat format = tournament.getFormatEntity();
        int teamSize;
        if(format == null) {
            teamSize = 1;
        }else{
            teamSize = format.getPlayersPerTeam();
        }
        if(teamSize > 1){
            participantDao.leaveTournamentTeam(userId, tournamentId);
        }else{
            participantDao.leaveTournamentUser(userId, tournamentId);
        }
        LOGGER.info("User with ID {} has successfully left tournament with ID {}", userId, tournamentId);
    }

    @Transactional
    @Override
    public void swapGroups(long tournamentId, long user1, long user2){
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if (tournament.getTournamentStarted()) {
            throw new TournamentAlreadyStartedException();
        }
        Integer g1 = participantDao.getGroupNumber(tournamentId, user1);
        Integer g2 = participantDao.getGroupNumber(tournamentId, user2);
        if(g1 == null || g2 == null) {
            throw new MissingGroupNumberException();
        }
        if (g1.equals(g2)) {
            LOGGER.warn("Cannot swap users within the same group");
            return;
        }
        participantDao.swapGroups(tournamentId, user1, user2, g1, g2);
        LOGGER.info("Users with IDs {} and {} have successfully swapped groups", user1, user2);
    }

    @Transactional
    @Override
    public void joinTournamentTeam(long tournamentId, long teamId, List<Long> participants){
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        Team team = teamDao.findById(teamId).orElseThrow(TeamNotFoundException::new);
        User creator = userDao.findById(tournament.getCreatorId()).orElseThrow(UserNotFoundException::new);

        for(Long p : participants){
            if(hasJoined(p, tournamentId)) {
                LOGGER.warn("User with ID {} has already joined tournament with ID {}", p, tournamentId);
                throw new UserAlreadyJoinedException();
            }
        }
        GameFormat format = tournament.getFormatEntity();
        int teamSize;
        if(format == null) {
            teamSize = 1;
        }else{
            teamSize = format.getPlayersPerTeam();
        }
        List<Participant> currentParticipants = getTournamentParticipants(tournamentId, teamSize);
        for(Long p : participants){
            if(p == null) {
                throw new ParticipantNotFoundException();
            }
            participantDao.joinTournamentUserWithTeam(p, tournamentId, teamId);
            User user = userDao.findById(p).orElseThrow(UserNotFoundException::new);
            ms.sendTournamentJoinedEmail(tournamentId, user.getUsername(), tournament.getName(), user.getEmail(), creator.getEmail());
            LOGGER.info("Tournament joined email correctly sent to the address {}", user.getEmail());
        }
        ms.sendTournamentTeamJoinedOwnerEmail(tournamentId, creator.getUsername(), team.getName(), tournament.getName(), creator.getEmail());
        LOGGER.info("Tournament joined notification email correctly sent to tournament owner with address {}", creator.getEmail());
        participantDao.joinTournamentTeam(tournamentId, teamId);
        if (currentParticipants.size() + 1 == tournament.getMaxParticipants()) {
            ts.closeInscriptions(tournamentId);
            LOGGER.info("Max participant count has been reached. The inscriptions for tournament with ID {} have been closed", tournamentId);
        }
    }

    @Override
    public boolean participantHasRatedTournament(long userId, long tournamentId) {
        return participantDao.hasRated(userId, tournamentId);
    }

    @Transactional
    @Override
    public void updateCreatorRating(long tournamentId, long creatorId, long reviewerId, float rating) {
        if(participantDao.hasRated(reviewerId, tournamentId)) {
            throw new ParticipantAlreadyRatedException();
        }
        participantDao.updateHasRated(reviewerId, tournamentId);
        User user = userDao.findById(creatorId).orElseThrow(UserNotFoundException::new);
        Float currentRating = user.getRating();
        float newRating = (currentRating == null) ? rating : (currentRating + rating) / 2;
        userDao.updateUserRating(creatorId, newRating);
    }

    @Transactional
    @Override
    public void removeParticipant(Long tournamentId, Long participantId){
        if(tournamentId == null) throw new TournamentNotFoundException();
        if(participantId == null) throw new ParticipantNotFoundException();
        Tournament tournament = tournamentDao.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        if(!tournament.getOpenInscriptions()){
            LOGGER.warn("Cannot remove participant from a closed tournament");
            return;
        }
        GameFormat format = tournament.getFormatEntity();
        int playersPerTeam;
        if(format == null){
            playersPerTeam = 1;
        }else{
            playersPerTeam = format.getPlayersPerTeam();
        }
        Participant participant = participantDao.getTournamentParticipantById(tournamentId, participantId, playersPerTeam);

        if(participant == null){
            LOGGER.warn("Participant with ID {} not found in tournament with ID {}", participantId, tournamentId);
            return;
        }

        if(playersPerTeam > 1){
            participantDao.leaveTournamentTeam(participant.getTeam().getId(), tournamentId);
            for(TeamMember teamMember : participant.getTeam().getTeamMembers()){
                ms.sendRemovedFromTournamentEmail(tournamentId, teamMember.getUser().getUsername(), tournament.getName(), teamMember.getUser().getEmail());
                LOGGER.info("Tournament joined email correctly sent to the address {}", teamMember.getUser().getEmail());
            }
        }else{
            participantDao.leaveTournamentUser(participant.getUser().getId(), tournamentId);
            ms.sendRemovedFromTournamentEmail(tournamentId, participant.getUser().getUsername(), tournament.getName(), participant.getUser().getEmail());
        }
        LOGGER.info("Participant with ID {} was successfully removed from tournament with ID {}", participantId, tournamentId);
    }
}
