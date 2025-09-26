package ar.edu.itba.paw.model.Tournament;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import java.time.LocalDate;

public class Tournament {

    private final Long id;
    private final Long creator_id;
    private final String name;
    private final Long game_id;
    private final Region region;
    private final Elo elo;
    private final LocalDate start_date;
    private final LocalDate end_date;
    private final String format;
    private final Structure structure;
    private final Integer max_participants;
    private final Integer image_id;
    private final Boolean open_inscriptions;
    private final Boolean is_finished;
    private final Long tournament_winner;
    private final Boolean is_group_stage;


    public Tournament(Long id, Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, Integer imageId, Boolean openInscriptions, Boolean isFinished) {
        this.id = id;
        this.creator_id = creator_id;
        this.name = name;
        this.game_id = game_id;
        this.region = region;
        this.elo = elo;
        this.start_date = start_date;
        this.end_date = end_date;
        this.format = format;
        this.structure = structure;
        this.max_participants = max_participants;
        this.image_id = imageId;
        this.open_inscriptions = openInscriptions;
        this.is_finished = isFinished;
        this.tournament_winner = null;
        this.is_group_stage = null;
    }

    public Long getId() {
        return id;
    }

    public Long getCreator_id() {
        return creator_id;
    }

    public String getName() {
        return name;
    }

    public Long getGame_id() {
        return game_id;
    }

    public Region getRegion() {
        return region;
    }

    public Elo getElo() {
        return elo;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public String getFormat() {
        return format;
    }

    public Structure getStructure() {
        return structure;
    }

    public Integer getMax_participants() {
        return max_participants;
    }

    public Integer getImage_id() {
        return image_id;
    }

    public Boolean getOpenInscriptions() {
        return open_inscriptions;
    }

    public Boolean getFinished() {
        return is_finished;
    }

    public Long getTournament_winner() {
        return tournament_winner;
    }

    public Boolean getIs_group_stage() {
        return is_group_stage;
    }
}
