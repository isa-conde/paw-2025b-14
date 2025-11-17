package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TournamentService {

    Optional<Tournament> findById(long id);

    List<Tournament> findTournaments(TournamentFilter tournamentFilter, long page);

    Tournament create(long creatorId, String name, long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format,
                      Structure structure, int maxParticipants, byte[] image, boolean openInscriptions, boolean isFinished, long formatId,
                      byte[] rules, String serverName, String serverPassword, String discordChannel);

    void setFinished(long tournamentId, long matchId);

    void closeInscriptions(long tournamentId);

    List<Tournament> searchByName(String name, long page);

    int countSearchByName(String name);

    void startTournament(long tournamentId);

    Map<Game,List<Tournament>> getUnfilteredTournamentPages(long page);

    int getPageAmount(int pageSize, TournamentFilter tf);

    void updateTournamentInfo(long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, byte[] image, String serverName, String serverPassword, String discordChannel);

    int getTournamentParticipantsCount(long tournamentId);

    void createBracketFromGroups(long tournamentId);

    void contactOwner(long tournamentId, User currentUser, String subject, String body, long creatorId);

    void updateTournamentRating(long tournamentId, float userRating);

    void notifyCreatorOfLeavingUser(User user, long tournamentId);

    List<Tournament> findUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won, Long page);

    int countUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won);

}


