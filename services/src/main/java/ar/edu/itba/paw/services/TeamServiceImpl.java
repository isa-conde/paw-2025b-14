package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class TeamServiceImpl implements TeamService {

    ImageDao imageDao;
    TeamDao teamDao;
    TeamMemberDao teamMemberDao;
    UserDao userDao;
    TournamentDao tournamentDao;
    ParticipantDao participantDao;
    TournamentService ts;

    public TeamServiceImpl(ImageDao imageDao, TeamDao teamDao, TeamMemberDao teamMemberDao, UserDao userDao, TournamentDao tournamentDao, ParticipantDao participantDao, TournamentService ts){
        this.imageDao = imageDao;
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
        this.userDao = userDao;
        this.tournamentDao = tournamentDao;
        this.participantDao = participantDao;
        this.ts = ts;
    }

    @Transactional
    @Override
    public Team create(String name, byte[] pfp, byte[] banner, Long owner_id, List<String> members) {
        Long pfp_id = null;
        Long banner_id = null;
        if (pfp != null){
            pfp_id = imageDao.insertImage(pfp);
        }
        if (banner != null){
            banner_id = imageDao.insertImage(banner);
        }

        Team team = teamDao.create(name, pfp_id, banner_id, owner_id);

        if (members != null){
            for (String s : members){
                teamMemberDao.addMember(team.getId(), userDao.findByUsername(s).get().getId());
            }
            User owner = userDao.findById(owner_id).get();
            if (!members.contains(owner.getUsername())) {
                teamMemberDao.addMember(team.getId(), owner_id);
            }
        }else{
            teamMemberDao.addMember(team.getId(), owner_id);
        }

        return team;
    }

    @Override
    public Optional<Team> getById(Long id) {
        return teamDao.getById(id);
    }

    @Override
    public List<Tournament> getActiveTournaments(Long team_id) {
        return getTournamentsFromIds(teamDao.getActiveTournaments(team_id));

    }

    @Override
    public List<Tournament> getPastTournaments(Long team_id) {
        return getTournamentsFromIds(teamDao.getPastTournaments(team_id));
    }

    @Override
    public List<Team> getUserTeams(Long user_id) {
        return teamDao.getUserTeams(user_id);
    }

    @Override
    public void updateTeam(Long teamId, String name, byte[] pfp, byte[] banner, List<String> members) {
        Optional<Team> optionalTeam = teamDao.getById(teamId);
        Long pfp_id = null;
        Long banner_id = null;
        if (pfp != null){
            pfp_id = imageDao.insertImage(pfp);
        }
        if (banner != null){
            banner_id = imageDao.insertImage(banner);
        }

        if (members != null){
            for (String s : members){
                Long userid =  userDao.findByUsername(s).get().getId();
                if (!teamMemberDao.isMember(teamId,userid)){
                    teamMemberDao.addMember(teamId, userid);
                }
            }
        }

        teamDao.updateTeam(teamId, name, pfp_id, banner_id);
    }

    @Override
    public Boolean isMember(Long team_id, Long user_id) {
        return teamMemberDao.isMember(team_id, user_id);
    }

    @Override
    public List<User> getTeamMembers(Long team_id) {
        List<Long> user_ids = teamMemberDao.getTeamMembers(team_id);
        List<User> toReturn = new ArrayList<>();
        for (Long l : user_ids){
            toReturn.add(userDao.findById(l).get());
        }
        return toReturn;
    }

    @Override
    public Boolean teamNameTaken(String name) {
        return teamDao.teamNameTaken(name);
    }

    @Override
    public List<Team> getUserTeamsByTournamentSize(Long userId, Long tournamentId) {
        return teamDao.getUserTeamsBySize(userId, ts.getPlayersPerTeam(tournamentId));
    }

    @Override
    public List<Team> searchByName(String name) {
        return teamDao.searchByName(name);
    }

    private List<Tournament> getTournamentsFromIds(List<Long> tournamentIds) {
        List<Tournament> tournaments = new ArrayList<>();
        for (Long id : tournamentIds) {
            tournamentDao.findById(id).ifPresent(tournaments::add);
        }
        return tournaments;
    }
}
