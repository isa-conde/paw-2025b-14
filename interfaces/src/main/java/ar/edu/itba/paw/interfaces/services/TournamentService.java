package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.ParticipantUserInfo;
import ar.edu.itba.paw.model.Tournament.TournamentImg;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.MatchWithPlayers;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TournamentService {

    public Optional<Tournament> findById(Long id);

    public List<Tournament> findTournaments(TournamentFilter tournamentFilter);

    public List<Tournament> findGameTournaments(Long game_id);

    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image_id, Boolean openInscriptions, Boolean isFinished);

    public void joinTournamentUser(Long user_id, Long tournament_id);

    public Map<Integer, List<ParticipantUserInfo>> getTournamentParticipantsByGroup(Long tournamentId);

    public Optional<Structure> getTournamentStructure(Long tournament_id);

    public void loadScores(Long match_id, Long tournament_id, Integer local_score, Integer visitor_score);

    //public void joinTournamentTeam(Long team_id, Long tournament_id);

    public List<TournamentImg> findWithImg(TournamentFilter tournamentFilter);

    public Optional<TournamentImg> findByIdWithImg(Long id);

    public List<TournamentImg> findByCreatorImg(Long creator_id);

    public void setFinished(Long tournament_id, Long match_id);

    public void closeInscriptions(Long tournament_id);

    public Boolean hasJoined(Long userId, Long tournamentId);

    public Map<Integer, Map<Integer, List<MatchWithPlayers>>> getTournamentMatchesByGroup(Long tournament_id);

    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    public List<TournamentImg> findUserActiveTournaments(Long userId);

    public List<TournamentImg> findUserPastTournaments(Long userId);

    public List<TournamentImg> searchByName(String name);

    public void startTournament(Long tournament_id);
}
