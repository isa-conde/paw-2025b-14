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

    void setFinished(Long tournamentId, Long matchId);

    void closeInscriptions(Long tournamentId);

    List<Tournament> searchByName(String name, Long page);

    int countSearchByName(String name);

    void startTournament(Long tournamentId);

    Map<Game,List<Tournament>> getUnfilteredTournamentPages(Long page);

    Integer getPageAmount(Integer pageSize, TournamentFilter tf);

    void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants, byte[] image, String serverName, String serverPassword, String discordChannel);

    int getTournamentParticipantsCount(Long tournamentId);

    void createBracketFromGroups(Long tournamentId);

    Integer getPlayersPerTeam(Long tournamentId);

    void contactOwner(Long tournamentId, User currentUser, String subject, String body, Long creatorId);

    void updateTouramentRating(Long tournamentId, Float userRating);

    void notifyCreatorOfLeavingUser(User user, long tournamentId);

    List<Tournament> findUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won, Long page);

    int countUserTournaments(Long userId, Boolean isFinished, Boolean isCreator, Boolean won);

}


