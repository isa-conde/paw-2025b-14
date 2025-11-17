package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamDao {

    Team create(String name, Long pfpId, Long bannerId, long ownerId);

    Optional<Team> findById(long id);

    List<Long> getPastTournaments(long teamId, int page);

    List<Long> getActiveTournaments(long teamId, int page);

    long getActivePages(long teamId);

    long getPastPages(long teamId);

    List<Team> getUserTeams(long userId);

    void updateTeam(long teamId, String name, Long pfpId, Long bannerId);

    boolean teamNameTaken(String name);

    List<Team> searchByName(String name, long page);

    List<Team> getUserTeamsBySizeNotInTournament(long userId, long tournamentId, long minSize);

    int countSearchByNameTeam(String name);
}
