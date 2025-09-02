package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TournamentDao {

    public Optional<Tournament> findById(Long id);

    public List<Tournament> findTournaments(TournamentFilter tournamentFilter);

    public Tournament create(Long creatorid, String name, Long gameid, Region region, Elo elo, LocalDate startdate, LocalDate enddate, String format, Structure structure, Integer max_participants);

    public void joinTournamentUser(Long user_id, Long tournament_id);

    //public void joinTournamentTeam(Long team_id, Long tournament_id);

    public List<User> getTournamentParticipants(Long tournament_id);

    public void createMatches(Long tournament_id);
    
    public Optional<Structure> getTournamentStructure(Long tournament_id);
    
    public void loadScores(Long match_id, Long tournament_id, Integer local_score, Integer visitor_score);
    
    public List<Match> getTournamentMatches(Long tournament_id);
}
