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

    Optional<Tournament> findById(long id);

    List<Tournament> findTournaments(TournamentFilter tournamentFilter, long page);

    Tournament create(long creatorId, String name, long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate,
                      String format, Structure structure, int maxParticipants, long imageId, boolean openInscriptions, boolean isFinished,
                      long formatId, Long rulesId, String serverName, String serverPassword, String discordChannel);

    Structure getTournamentStructure(long tournamentId);

    List<Tournament> findByCreator(long creatorId, long page, boolean isFinished);

    void setFinished(long tournamentId);

    List<Tournament> findUserActiveTournaments(long userId, long page);

    List<Tournament> findUserPastTournaments(long userId, long page);

    void closeInscriptions(long tournamentId);

    List<Tournament> searchByName(String name);

    void startTournament(long tournamentId);

    Map<Long,List<Tournament>> getUnfilteredTournamentPages(long page);

    int getPageAmount(int pageSize, TournamentFilter tf);

    void updateTournamentInfo(long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, String serverName, String serverPassword, String discordChannel);

    int getTournamentParticipantsCount(long tournamentId);

    void setIsGroupStage(long tournamentId, boolean bool);

    Boolean getIsGroupStage(long tournamentId);

    void setTournamentWinner(long tournamentId, long winnerId);

    boolean isTournamentStarted(long tournamentId);

    void updateAllStartDates();

    void updateAllEndDates();

    boolean isClosed(long tournamentId);

    int getUserPastTournamentsPages(long userId);

    int getUserActiveTournamentsPages(long userId);

    int getCreatedAndOngoingTournamentsPages(long userId);

    int getCreatedAndFinishedTournamentsPages(long userId);

    void updateTournamentRating(long tournamentId, float userRating);

    List <Tournament> getUserWonTournament(long userId, long page);

    int getUserWonTournamentPages(long userId);
}
