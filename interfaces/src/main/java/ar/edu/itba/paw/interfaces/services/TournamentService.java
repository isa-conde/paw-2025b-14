package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.model.TournamentImg;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TournamentService {

    public Optional<Tournament> findById(Long id);

    public List<Tournament> findTournaments(TournamentFilter tournamentFilter);

    public List<Tournament> findGameTournaments(Long game_id);

    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image_id, Boolean openInscriptions, Boolean isFinished);

    public void joinTournamentUser(Long user_id, Long tournament_id);

    public void joinTournamentTeam(Long team_id, Long tournament_id);

    public List<TournamentImg> findWithImg(TournamentFilter tournamentFilter);

    public Optional<TournamentImg> findByIdWithImg(Long id);

    public List<TournamentImg> findByCreatorImg(Long creator_id);

    public void setFinished (Long tournament_id);

}
