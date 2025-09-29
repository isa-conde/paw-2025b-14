package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.ParticipantUserInfo;
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

    public Map<Integer, List<ParticipantUserInfo>> getTournamentParticipantsByGroup(Long tournamentId);

    public Optional<Structure> getTournamentStructure(Long tournament_id);

    //public void joinTournamentTeam(Long team_id, Long tournament_id);

    public List<Tournament> findByCreator(Long creator_id);

    public void setFinished(Long tournament_id, Long match_id);

    public void closeInscriptions(Long tournament_id);

    public Map<Integer, Map<Integer, List<MatchWithPlayers>>> getTournamentMatchesByGroup(Long tournament_id);

    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner);

    public List<Tournament> findUserActiveTournaments(Long userId);

    public List<Tournament> findUserPastTournaments(Long userId);

    public List<Tournament> searchByName(String name);

    public void startTournament(Long tournament_id);

    void swapGroups(Long tournament_id, Long user1, Long user2);

    void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2);
}
