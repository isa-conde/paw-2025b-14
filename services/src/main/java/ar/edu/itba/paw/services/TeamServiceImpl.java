package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.ImageNotFoundException;
import ar.edu.itba.paw.interfaces.exception.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.exception.TournamentNotFoundException;
import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.interfaces.services.TournamentService;
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
    public Team create(String name, byte[] pfp, byte[] banner, long ownerId, List<String> members) {
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
            User owner = userDao.findById(ownerId).orElseThrow(UserNotFoundException::new);
            if (!members.contains(owner.getUsername())) {
                teamMemberDao.addMember(team.getId(), ownerId);
            }
        } else {
            teamMemberDao.addMember(team.getId(), ownerId);
        }

        return team;
    }

    @Override
    public Optional<Team> findById(long id) {
        return teamDao.findById(id);
    }

    @Override
    public List<Tournament> getActiveTournaments(long teamId, int page) {
        return getTournamentsFromIds(teamDao.getActiveTournaments(teamId, page));
    }

    @Override
    public List<Tournament> getPastTournaments(long teamId, int page) {
        return getTournamentsFromIds(teamDao.getPastTournaments(teamId, page));
    }

    @Override
    public long getActivePages(long teamId) {
        return teamDao.getActivePages(teamId);
    }

    @Override
    public long getPastPages(long teamId) {
        return teamDao.getPastPages(teamId);
    }

    @Override
    public List<Team> getUserTeams(long userId) {
        return teamDao.getUserTeams(userId);
    }

    @Transactional
    @Override
    public void updateTeam(long teamId, String name, byte[] pfp, byte[] banner, List<String> members) {
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
                long userId = userDao.findByUsername(s).orElseThrow(UserNotFoundException::new).getId();
                if (!teamMemberDao.isMember(teamId, userId)){
                    teamMemberDao.addMember(teamId, userId);
                }
            }
        }

        teamDao.updateTeam(teamId, name, pfpId, bannerId);
    }

    @Override
    public boolean isMember(Long teamId, Long userId) {
        return teamMemberDao.isMember(teamId, userId);
    }

    @Transactional
    @Override
    public List<User> getTeamMembers(Long teamId) {
        if(teamId == null) {
            throw new TeamNotFoundException();
        }
        return teamDao.findById(teamId).orElseThrow(TeamNotFoundException::new).getMembers();
    }

    @Override
    public boolean teamNameTaken(String name) {
        return teamDao.teamNameTaken(name);
    }

    @Override
    public List<Team> getUserTeamsBySizeNotInTournament(long userId, long tournamentId) {
        return teamDao.getUserTeamsBySizeNotInTournament(userId, tournamentId, ts.getPlayersPerTeam(tournamentId));
    }

    @Override
    public List<Team> searchByName(String name) {
        return teamDao.searchByName(name);
    }

    private List<Tournament> getTournamentsFromIds(List<Long> tournamentIds) {
        List<Tournament> tournaments = new ArrayList<>();
        for (Long id : tournamentIds) {
            if(id == null) {
                throw new TournamentNotFoundException();
            }
            tournamentDao.findById(id).ifPresent(tournaments::add);
        }
        return tournaments;
    }
}
