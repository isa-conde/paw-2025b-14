package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    Team create(String name, byte[] pfp, byte[] banner, long ownerId, List<String> members);

    Optional<Team> findById(long id);

    List<Tournament> getActiveTournaments(long teamId, int page);

    List<Tournament> getPastTournaments(long teamId, int page);

    long getActivePages(long teamId);

    long getPastPages(long teamId);

    List<Team> getUserTeams(long userId);

    void updateTeam(long teamId, String name, byte[] pfp, byte[] banner, List<String> members);

    boolean isMember(Long teamId, Long userId);

    List<User> getTeamMembers(Long teamId);

    boolean teamNameTaken(String name);

    List<Team> getUserTeamsBySizeNotInTournament(long userId, long tournamentId);

    List<Team> searchByName(String name);
}
