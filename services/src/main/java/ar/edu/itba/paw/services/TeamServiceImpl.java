package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
import ar.edu.itba.paw.model.Game.GameFormat;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
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
    public Team create(String name, byte[] pfp, byte[] banner, Long ownerId, List<String> members) {
        User owner = userDao.findById(ownerId).orElseThrow(UserNotFoundException::new);
        Long pfpId = null;
        Long bannerId = null;
        if (pfp != null){
            pfpId = imageDao.insertImage(pfp);
        }
        if (banner != null){
            bannerId = imageDao.insertImage(banner);
        }

        Team team = teamDao.create(name, pfpId, bannerId, ownerId);
        LOGGER.info("The team {} has been successfully created", name);

        if (members != null){
            for (String s : members){
                teamMemberDao.addMember(team.getId(), userDao.findByUsername(s).orElseThrow(UserNotFoundException::new).getId());
            }
            if (!members.contains(owner.getUsername())) {
                teamMemberDao.addMember(team.getId(), ownerId);
            }
        } else {
            teamMemberDao.addMember(team.getId(), ownerId);
        }

        return team;
    }

    @Override
    public Optional<Team> findById(Long id) {
        return teamDao.findById(id);
    }

    @Override
    public List<Tournament> getActiveTournaments(Long teamId, Integer page) {
        return getTournamentsFromIds(teamDao.getActiveTournaments(teamId, page));
    }

    @Override
    public List<Tournament> getPastTournaments(Long teamId, Integer page) {
        return getTournamentsFromIds(teamDao.getPastTournaments(teamId, page));
    }

    @Override
    public Long getActivePages(Long teamId) {
        return teamDao.getActivePages(teamId);
    }

    @Override
    public Long getPastPages(Long teamId) {
        return teamDao.getPastPages(teamId);
    }

    @Override
    public List<Team> getUserTeams(Long userId) {
        return teamDao.getUserTeams(userId);
    }

    @Transactional
    @Override
    public void updateTeam(Long teamId, String name, byte[] pfp, byte[] banner, List<String> members) {
        Long pfpId = null;
        Long bannerId = null;
        if (pfp != null){
            pfpId = imageDao.insertImage(pfp);
        }
        if (banner != null){
            bannerId = imageDao.insertImage(banner);
        }

        if (members != null){
            for (String s : members){
                Long userId =  userDao.findByUsername(s).orElseThrow(UserNotFoundException::new).getId();
                if (!teamMemberDao.isMember(teamId, userId)){
                    teamMemberDao.addMember(teamId, userId);
                }
            }
        }

        teamDao.updateTeam(teamId, name, pfpId, bannerId);
    }

    @Transactional
    @Override
    public List<User> getTeamMembers(Long teamId) {
        return teamDao.findById(teamId).orElseThrow(TeamNotFoundException::new).getMembers();
    }

    @Override
    public Boolean teamNameTaken(String name) {
        return teamDao.teamNameTaken(name);
    }

    @Override
    public List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId) {
        Tournament tournament = ts.findById(tournamentId).orElseThrow(TournamentNotFoundException::new);
        GameFormat format = tournament.getFormatEntity();
        long teamSize;
        if(format == null) {
            teamSize = 1;
        }else{
            teamSize = format.getPlayersPerTeam();
        }
        return teamDao.getUserTeamsBySizeNotInTournament(userId, tournamentId, teamSize);
    }

    @Override
    public int countSearchByNameTeam(String name) {
        return teamDao.countSearchByNameTeam(name);
    }

    @Override
    public List<Team> searchByName(String name, Long page) {
        return teamDao.searchByName(name, page);
    }

    private List<Tournament> getTournamentsFromIds(List<Long> tournamentIds) {
        List<Tournament> tournaments = new ArrayList<>();
        for (Long id : tournamentIds) {
            tournamentDao.findById(id).ifPresent(tournaments::add);
        }
        return tournaments;
    }
}
