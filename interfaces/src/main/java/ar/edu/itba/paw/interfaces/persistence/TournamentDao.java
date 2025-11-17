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

    void setFinished(long tournamentId);

    void closeInscriptions(long tournamentId);

    List<Tournament> searchByName(String name, long page);

    int countSearchByName(String name);

    void startTournament(long tournamentId);

    Map<Long,List<Tournament>> getUnfilteredTournamentPages(long page);

    int getPageAmount(int pageSize, TournamentFilter tf);

    void updateTournamentInfo(long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, String serverName, String serverPassword, String discordChannel);

    int getTournamentParticipantsCount(long tournamentId);

    void setIsGroupStage(long tournamentId, boolean bool);

    Boolean getIsGroupStage(long tournamentId);

    void setTournamentWinner(long tournamentId, long winnerId);

    void updateAllStartDates();

    void updateAllEndDates();

    void updateTournamentRating(long tournamentId, float userRating);

    List<Tournament> findUserTournaments(long userId, boolean isFinished, boolean isCreator, boolean won, long page);

    int countUserTournaments(long userId, boolean isFinished, boolean isCreator, boolean won);
}
