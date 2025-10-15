package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    Team create(String name, byte[] pfp, byte[] banner, Long owner_id, List<String> members);

    Optional<Team> getById(Long id);

    List<Tournament> getActiveTournaments(Long team_id, Integer page);

    List<Tournament> getPastTournaments(Long team_id, Integer page);

    Integer getActivePages(Long team_id);

    Integer getPastPages(Long team_id);

    List<Team> getUserTeams(Long user_id);

    void updateTeam(Long teamId, String name, byte[] pfp, byte[] banner, List<String> members);

    Boolean isMember(Long team_id, Long user_id);

    List<User> getTeamMembers(Long team_id);

    Boolean teamNameTaken(String name);

    List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId);

    List<Team> searchByName(String name);
}
