package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import java.time.LocalDate;
import java.util.Date;

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



    public Tournament(Long id, Long creatorId, String name, Long gameId, Region region, Elo elo, LocalDate startDate, LocalDate endDate, String format, Structure structure, Integer maxParticipants) {
        this.id = id;
        creator_id = creatorId;
        this.name = name;
        game_id = gameId;
        this.region = region;
        this.elo = elo;
        start_date = startDate;
        end_date = endDate;
        this.format = format;
        this.structure = structure;
        max_participants = maxParticipants;
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
}
