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

    Optional<Tournament> findById(Long id);

    List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page);

    Tournament create(Long creatorId, String name, Long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format,
                      Structure structure, Integer maxParticipants, byte[] imageId, Boolean openInscriptions, Boolean isFinished,
                      Long formatId, byte[] rulesId, String serverName, String serverPassword, String discordChannel);

    List<Tournament> findByCreator(Long creatorId, Long page, Boolean isFinished);

    void setFinished(long tournamentId, long matchId);

    void closeInscriptions(Long tournamentId);

    List<Tournament> findUserActiveTournaments(Long userId, Long page);

    List<Tournament> findUserPastTournaments(Long userId, Long page);

    List<Tournament> searchByName(String name);

    void startTournament(Long tournamentId);

    Map<Game,List<Tournament>> getUnfilteredTournamentPages(Long page);

    int getPageAmount(int pageSize, TournamentFilter tf);

    void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, byte[] image, String serverName, String serverPassword, String discordChannel);

    int getTournamentParticipantsCount(Long tournamentId);

    void createBracketFromGroups(long tournamentId);

    List<Tournament> getCreatedAndFinishedTournaments(Long userId, Long page);

    List<Tournament> getCreatedAndOngoingTournaments(Long userId, Long page);

    int getPlayersPerTeam(Long tournamentId);

    int getPagesBySection(Long userId, String section);

    List <Tournament> getUserWonTournament(Long userId, Long page);

    int getUserWonTournamentPages(Long userId);

    void contactOwner(Long tournamentId, User currentUser, String subject, String body, Long creatorId);

    void updateTournamentRating(Long tournamentId, Float userRating);

    void notifyCreatorOfLeavingUser(User user, long tournamentId);
}


