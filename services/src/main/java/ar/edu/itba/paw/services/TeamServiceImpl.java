package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    ImageDao imageDao;
    TeamDao teamDao;
    TeamMemberDao teamMemberDao;
    UserDao userDao;
    TournamentDao tournamentDao;
    ParticipantDao participantDao;

    public TeamServiceImpl(ImageDao imageDao, TeamDao teamDao, TeamMemberDao teamMemberDao, UserDao userDao, TournamentDao tournamentDao, ParticipantDao participantDao){
        this.imageDao = imageDao;
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
        this.userDao = userDao;
        this.tournamentDao = tournamentDao;
        this.participantDao = participantDao;
    }


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

        for (String s : members){
            teamMemberDao.AddMember(team.getId(), userDao.findByUsername(s).get().getId());
        }
        teamMemberDao.AddMember(team.getId(), owner_id);


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

        for (String s : members){
            if (!teamMemberDao.isMember(teamId, userDao.findByUsername(s).get().getId())){
                teamMemberDao.AddMember(teamId, userDao.findByUsername(s).get().getId());
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

    private List<Tournament> getTournamentsFromIds(List<Long> tournamentIds) {
        List<Tournament> tournaments = new ArrayList<>();
        for (Long id : tournamentIds) {
            tournamentDao.findById(id).ifPresent(tournaments::add);
        }
        return tournaments;
    }
}
