package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.Game.Game;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TournamentService {

    Optional<Tournament> findById(Long id);

    List<Tournament> findTournaments(TournamentFilter tournamentFilter, Long page);

    Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image_id, Boolean openInscriptions, Boolean isFinished, Long format_id);

    List<Tournament> findByCreator(Long creator_id, Long page, Boolean isFinished);

    void setFinished(Long tournament_id, Long match_id);

    void closeInscriptions(Long tournament_id);

    List<Tournament> findUserActiveTournaments(Long userId, Long page);

    List<Tournament> findUserPastTournaments(Long userId, Long page);

    List<Tournament> searchByName(String name);

    void startTournament(Long tournament_id);

    Map<Game,List<Tournament>> getUnfilteredTournamentPages(Long page);

    Integer getPageAmount(Integer pageSize, TournamentFilter tf);

    void updateTournamentInfo(Long tournament_id, String name, LocalDate start_date, LocalDate end_date, Integer max_participants, byte[] image);

    int getTournamentParticipantsCount(Long tournamentId);

    void createBracketFromGroups(Long tournamentId, Long lastMatchId);

    List<Tournament> getCreatedAndFinishedTournaments(Long userId, Long page);

    List<Tournament> getCreatedAndOngoingTournaments(Long userId, Long page);

    Integer getPlayersPerTeam(Long tournamentId);

    Integer getPagesBySection(Long userId, String section);

    void contactOwner(Long tournamentId, User currentUser, String subject, String body, Long creatorId);

}


