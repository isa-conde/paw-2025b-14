package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface TeamDao {

    Team create(String name, Long pfp_id, Long banner_id, Long owner_id);

    Optional<Team> getById(Long id);

    List<Long> getPastTournaments(Long team_id);

    List<Long> getActiveTournaments(Long teamId);

    List<Team> getUserTeams(Long user_id);

    void updateTeam(Long teamId, String name, Long pfpId, Long bannerId);

    Boolean teamNameTaken(String name);

    List<Team> searchByName(String name);

    }
