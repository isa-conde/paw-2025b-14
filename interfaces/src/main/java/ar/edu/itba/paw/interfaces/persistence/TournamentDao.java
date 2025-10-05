package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.MatchInfo;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TournamentDao {

    Optional<Tournament> findById(Long id);

    List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page);

    List<Tournament> findGameTournaments(Long game_id);

    Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, Long image_id, Boolean openInscriptions, Boolean isFinished, Long format_id);

    Structure getTournamentStructure(Long tournament_id);

    List<Tournament> findByCreator(Long creator_id);

    void setFinished(Long tournament_id);

    List<Tournament> findUserActiveTournaments(Long userId);

    List<Tournament> findUserPastTournaments(Long userId);

    void closeInscriptions(Long tournament_id);

    List<Tournament> searchByName(String name);

    void startTournament(Long tournament_id);

    Map<Long,List<Tournament>> getUnfilteredTournamentPages(Long page);

    Integer getPageAmount(Integer pageSize, TournamentFilter tf);

    void updateTournamentInfo(Long tournament_id, String name, LocalDate start_date, LocalDate end_date, Integer max_participants);

    int tournamentParticipantsCount(Long tournamentId);

    void setIsGroupStage(Long tournamentId, Boolean bool);

    Boolean getIsGroupStage(Long tournamentId);

    void setTournamentWinner(Long tournament_id, Long user_id);

    Boolean isTournamentStarted(Long tournament_id);

    void updateAllStartDates();

    void updateAllEndDates();

    boolean isClosed(Long tournamentId);

    boolean hasWinner(Long matchId, Long tournamentId);
}
