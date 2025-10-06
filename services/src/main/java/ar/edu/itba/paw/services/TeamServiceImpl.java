package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.Team;
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
public class TeamServiceImpl implements TeamService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final ImageDao imageDao;
    private final TeamDao teamDao;
    private final TeamMemberDao teamMemberDao;
    private final UserDao userDao;
    private final TournamentDao tournamentDao;

    public TeamServiceImpl(ImageDao imageDao, TeamDao teamDao, TeamMemberDao teamMemberDao, UserDao userDao, TournamentDao tournamentDao){
        this.imageDao = imageDao;
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
        this.userDao = userDao;
        this.tournamentDao = tournamentDao;
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

        for (String s : members){
            teamMemberDao.addMember(team.getId(), userDao.findByUsername(s).get().getId()); // TODO: check if member is already in team?
            LOGGER.info("User {} has been successfully added to team {}", s, name);
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

    private List<Tournament> getTournamentsFromIds(List<Long> tournamentIds) {
        List<Tournament> tournaments = new ArrayList<>();
        for (Long id : tournamentIds) {
            tournamentDao.findById(id).ifPresent(tournaments::add);
        }
        return tournaments;
    }


}
