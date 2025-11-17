package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TournamentDao {

    Optional<Tournament> findById(Long id);

    List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page);

    Tournament create(Long creatorId, String name, Long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate,
                      String format, Structure structure, Integer maxParticipants, Long imageId, Boolean openInscriptions, Boolean isFinished,
                      Long formatId, Long rulesId, String serverName, String serverPassword, String discordChannel);

    Structure getTournamentStructure(Long tournamentId);

    void setFinished(Long tournamentId);

    void closeInscriptions(Long tournamentId);

    List<Tournament> searchByName(String name, Long page);

    int countSearchByName(String name);

    void startTournament(Long tournamentId);

    Map<Long,List<Tournament>> getUnfilteredTournamentPages(Long page);

    Integer getPageAmount(Integer pageSize, TournamentFilter tf);

    void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, String serverName, String serverPassword, String discordChannel);

    int getTournamentParticipantsCount(Long tournamentId);

    void setIsGroupStage(Long tournamentId, Boolean bool);

    Boolean getIsGroupStage(Long tournamentId);

    void setTournamentWinner(Long tournamentId, Long winnerId);

    Boolean isTournamentStarted(Long tournamentId);

    void updateAllStartDates();

    void updateAllEndDates();

    boolean isClosed(Long tournamentId);

    void updateTournamentRating(Long tournamentId, Float userRating);

    List<Tournament> findUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won, Long page);

    int countUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won);
}
