package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    Team create(String name, byte[] pfp, byte[] banner, Long ownerId, List<String> members);

    Optional<Team> findById(Long id);

    List<Tournament> getActiveTournaments(Long teamId, Integer page);

    List<Tournament> getPastTournaments(Long teamId, Integer page);

    long getActivePages(Long teamId);

    long getPastPages(Long teamId);

    List<Team> getUserTeams(Long userId);

    void updateTeam(Long teamId, String name, byte[] pfp, byte[] banner, List<String> members);

    boolean isMember(Long teamId, Long userId);

    List<User> getTeamMembers(Long teamId);

    boolean teamNameTaken(String name);

    List<Team> getUserTeamsBySizeNotInTournament(Long userId, Long tournamentId);

    List<Team> searchByName(String name);
}
