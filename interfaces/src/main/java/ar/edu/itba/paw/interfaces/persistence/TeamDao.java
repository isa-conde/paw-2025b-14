package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamDao {

    Team create(String name, Long pfpId, Long bannerId, Long ownerId);

    Optional<Team> findById(Long id);

    List<Long> getPastTournaments(Long teamId, Integer page);

    List<Long> getActiveTournaments(Long teamId, Integer page);

    Long getActivePages(Long teamId);

    Long getPastPages(Long teamId);

    List<Team> getUserTeams(Long userId);

    void updateTeam(Long teamId, String name, Long pfpId, Long bannerId);

    Boolean teamNameTaken(String name);

    List<Team> searchByName(String name);

    List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId, Long minSize);
}
