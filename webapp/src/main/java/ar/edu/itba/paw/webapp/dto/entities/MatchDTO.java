package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Match.Match;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MatchDTO {

    private long id;
    private Integer localScore;
    private Integer visitorScore;
    private Integer winner;
    private Integer stage;
    private Boolean groupStage;
    private LocalDate date;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Match, MatchDTO> mapper(final UriInfo uriInfo) {
        return (match) -> fromMatch(uriInfo, match);
    }

    public static MatchDTO fromMatch(final UriInfo uriInfo, final Match match) {
        MatchDTO toReturn = new MatchDTO();

        toReturn.id = match.getId();
        toReturn.localScore = match.getLocalScore();
        toReturn.visitorScore = match.getVisitorScore();
        toReturn.winner = match.getWinner();
        toReturn.stage = match.getStage();
        toReturn.groupStage = match.getGroupStage();
        toReturn.date = match.getDate();

        toReturn.addLink("self", uriInfo.getBaseUriBuilder().path("tournaments")
                .path(String.valueOf(match.getTournamentId()))
                .path("matches")
                .path(String.valueOf(match.getId()))
                .build().toString());
        toReturn.addLink("tournament", uriInfo.getBaseUriBuilder().path("tournaments")
                .path(String.valueOf(match.getTournamentId())).build().toString());

        if (match.getLocalId() != null) {
            toReturn.addLink("localParticipant", uriInfo.getBaseUriBuilder().path("participants")
                    .path(String.valueOf(match.getLocalId())).build().toString());
        }

        if (match.getVisitorId() != null) {
            toReturn.addLink("visitorParticipant", uriInfo.getBaseUriBuilder().path("participants")
                    .path(String.valueOf(match.getVisitorId())).build().toString());
        }

        return toReturn;
    }

    private void addLink(String rel, String href) {
        links.add(new LinkDTO(rel, href));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getLocalScore() {
        return localScore;
    }

    public void setLocalScore(Integer localScore) {
        this.localScore = localScore;
    }

    public Integer getVisitorScore() {
        return visitorScore;
    }

    public void setVisitorScore(Integer visitorScore) {
        this.visitorScore = visitorScore;
    }

    public Integer getWinner() {
        return winner;
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Boolean getGroupStage() {
        return groupStage;
    }

    public void setGroupStage(Boolean groupStage) {
        this.groupStage = groupStage;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}