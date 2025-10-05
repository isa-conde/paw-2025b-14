package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Team;
import ar.edu.itba.paw.model.Tournament.Tournament;

import java.util.List;
import java.util.Optional;

public interface TeamService {

    Team create(String name, byte[] pfp, byte[] banner, Long owner_id, List<String> members);

    Optional<Team> getById(Long id);

    List<Tournament> getActiveTournaments(Long team_id);

    List<Tournament> getPastTournaments(Long team_id);

    List<Team> getUserTeams(Long user_id);

}
