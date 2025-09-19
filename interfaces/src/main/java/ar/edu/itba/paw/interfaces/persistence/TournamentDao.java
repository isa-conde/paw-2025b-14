package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Tournament.TournamentImg;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Pair;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TournamentDao {

    public Optional<Tournament> findById(Long id);

    public List<Tournament> findTournaments(TournamentFilter tournamentFilter);

    public List<Tournament> findGameTournaments(Long game_id);

    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image_id, Boolean openInscriptions, Boolean isFinished);

    public void joinTournamentUser(Long user_id, Long tournament_id);

    //public void joinTournamentTeam(Long team_id, Long tournament_id);

    public List<User> getTournamentUsers(Long tournament_id);

    List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id);

    public void createMatches(Long tournament_id);

    public Optional<Structure> getTournamentStructure(Long tournament_id);

    public void loadScores(Long match_id, Long tournament_id, Integer local_score, Integer visitor_score);

    public List<Match> getTournamentMatches(Long tournament_id);

    public List<MatchWithPlayers> getTournamentMatchesWithPlayers(Long tournament_id);

    public List<TournamentImg> findWithImg(TournamentFilter tournamentFilter);

    public Optional<TournamentImg> findByIdWithImg(Long id);

    public List<TournamentImg> findByCreatorImg(Long creator_id);

    public void setFinished(Long tournament_id);

    public Boolean hasJoined(Long userId, Long tournamentId);

    public List<TournamentImg> findUserActiveTournaments(Long userId);

    public List<TournamentImg> findUserPastTournaments(Long userId);
    public void closeInscriptions(Long tournament_id);

    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    public List<TournamentImg> searchByName(String name);
}
