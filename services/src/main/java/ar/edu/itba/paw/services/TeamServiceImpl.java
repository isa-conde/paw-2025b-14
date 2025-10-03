package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.TeamDao;
import ar.edu.itba.paw.interfaces.persistence.TeamMemberDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.TeamService;
import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    ImageDao imageDao;
    TeamDao teamDao;
    TeamMemberDao teamMemberDao;
    UserDao userDao;

    public TeamServiceImpl(ImageDao imageDao, TeamDao teamDao, TeamMemberDao teamMemberDao, UserDao userDao){
        this.imageDao = imageDao;
        this.teamDao = teamDao;
        this.teamMemberDao = teamMemberDao;
        this.userDao = userDao;
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

        return team;
    }
}
