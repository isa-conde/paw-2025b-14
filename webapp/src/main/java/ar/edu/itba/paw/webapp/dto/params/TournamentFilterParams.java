package ar.edu.itba.paw.webapp.dto.params;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Genre;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;

import javax.ws.rs.QueryParam;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TournamentFilterParams {

    @QueryParam("name")
    private String name;

    @QueryParam("gameId")
    private Long gameId;

    @QueryParam("region")
    private Region region;

    @QueryParam("elo")
    private Elo elo;

    @QueryParam("startDate")
    private String startDate;

    @QueryParam("endDate")
    private String endDate;

    @QueryParam("format")
    private String format;

    @QueryParam("structure")
    private Structure structure;

    @QueryParam("genre")
    private Genre genre;

    @QueryParam("playersPerTeam")
    private Integer playersPerTeam;

    public TournamentFilter toFilter() {
        TournamentFilter filter = new TournamentFilter();
        filter.setName(normalize(name));
        filter.setGameId(gameId);
        filter.setRegion(region);
        filter.setElo(elo);
        filter.setStartDate(parseDate(startDate, "startDate"));
        filter.setEndDate(parseDate(endDate, "endDate"));
        filter.setFormat(normalize(format));
        filter.setStructure(structure);
        filter.setGenre(genre);
        filter.setPlayersPerTeam(playersPerTeam);
        return filter;
    }

    public Long getGameId() {
        return gameId;
    }

    public Integer getPlayersPerTeam() {
        return playersPerTeam;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid " + fieldName + " date format, expected YYYY-MM-DD.");
        }
    }
}
