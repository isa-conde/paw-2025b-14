package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.GameDao;
import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.interfaces.services.ParticipantService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Participant;
import ar.edu.itba.paw.model.Tournament.Tournament;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    ParticipantDao participantDao;
    TournamentDao tournamentDao;
    GameDao gameDao;
    TournamentService ts;

    public ParticipantServiceImpl(ParticipantDao participantDao, TournamentDao tournamentDao, TournamentService ts, GameDao gameDao){
        this.participantDao = participantDao;
        this.tournamentDao = tournamentDao;
        this.ts = ts;
        this.gameDao = gameDao;
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        participantDao.joinTournamentUser(user_id, tournament_id);

        List<Participant> participants = getTournamentParticipantUsers(tournament_id);
        Optional<Tournament> tournament = tournamentDao.findById(tournament_id);
        if (tournament.isPresent() && participants.size() == tournament.get().getMax_participants()) {
            ts.closeInscriptions(tournament_id);
        }
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
        participants.sort((a, b) -> b.getPoints().compareTo(a.getPoints()));
        return participants;
    }

    @Override
    public Participant getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
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
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2){
        if (tournamentDao.isTournamentStarted(tournament_id)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started");
        }
        Optional<Tournament> t = tournamentDao.findById(tournament_id);
        Integer teamSize = gameDao.getFormatById(t.get().getFormat_id()).get().getPlayers_per_team();

        Integer g1 = participantDao.getGroupNumber(tournament_id, user1, teamSize);
        Integer g2 = participantDao.getGroupNumber(tournament_id, user2, teamSize);

        if (g1.equals(g2)) {
            return;
        }

        participantDao.swapGroups(tournament_id, user1, user2, g1, g2, teamSize);
    }

    @Override
    public void joinTournamentTeam(Long tournamentId, Long teamId, List<Long> participants){
        for(Long p : participants){
            participantDao.joinTournamentUserWithTeam(p, tournamentId, teamId);
        }
        participantDao.joinTournamentTeam(tournamentId, teamId);
    }
}
