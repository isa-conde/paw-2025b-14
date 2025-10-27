package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class TeamServiceImpl implements TeamService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final ImageDao imageDao;
    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;
    private final UserDao userDao;
    private final TournamentDao tournamentDao;
    private final TournamentService ts;


    public TeamServiceImpl(ImageDao imageDao, TeamDao teamDao, TeamMemberDao teamMemberDao, UserDao userDao, TournamentDao tournamentDao, TournamentService ts){
        this.imageDao = imageDao;
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
        this.userDao = userDao;
        this.tournamentDao = tournamentDao;
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
        LOGGER.info("The team {} has been successfully created", name);

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
    public List<Tournament> getActiveTournaments(Long team_id, Integer page) {
        return getTournamentsFromIds(teamDao.getActiveTournaments(team_id, page));
    }

    @Override
    public List<Tournament> getPastTournaments(Long team_id, Integer page) {
        return getTournamentsFromIds(teamDao.getPastTournaments(team_id, page));
    }

    @Override
    public Long getActivePages(Long team_id) {
        return teamDao.getActivePages(team_id);
    }

    @Override
    public Long getPastPages(Long team_id) {
        return teamDao.getPastPages(team_id);
    }

    @Override
    public List<Team> getUserTeams(Long user_id) {
        return teamDao.getUserTeams(user_id);
    }

    @Transactional
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

    @Transactional
    @Override
    public List<User> getTeamMembers(Long team_id) {
        Optional<Team> t = teamDao.getById(team_id);
        if (t.isEmpty()){
            LOGGER.warn("Team not found for teamId={} ",team_id);
            throw new IllegalArgumentException();
        }
        return t.get().getMembers();
    }

    @Override
    public Boolean teamNameTaken(String name) {
        return teamDao.teamNameTaken(name);
    }

    @Override
    public List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId) {
        return teamDao.getUserTeamsBySizeNotInTournament(userId, tournamentId, (long)ts.getPlayersPerTeam(tournamentId));
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
