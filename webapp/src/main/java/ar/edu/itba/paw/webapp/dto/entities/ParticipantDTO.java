package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Participant;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ParticipantDTO {

    private long id;
    private String name;
    private int points;
    private Integer scoreDifference;
    private Integer groupNumber;
    private boolean hasRated;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Participant, ParticipantDTO> mapper(final UriInfo uriInfo) {
        return (participant) -> fromParticipant(uriInfo, participant);
    }

    public static ParticipantDTO fromParticipant(final UriInfo uriInfo, final Participant participant) {
        ParticipantDTO toReturn = new ParticipantDTO();

        toReturn.id = participant.getId();
        toReturn.name = participant.getName();
        toReturn.points = participant.getPoints();
        toReturn.scoreDifference = participant.getScoreDifference();
        toReturn.groupNumber = participant.getGroupNumber();
        toReturn.hasRated = Boolean.TRUE.equals(participant.getHasRated());

        toReturn.addLink("self", uriInfo.getAbsolutePathBuilder().path("participants")
                .path(String.valueOf(participant.getId())).build().toString());

        if (participant.getUser() != null) {
            toReturn.addLink("user", uriInfo.getAbsolutePathBuilder().path("users")
                    .path(String.valueOf(participant.getUser().getId())).build().toString());
        }

        if (participant.getTeam() != null) {
            toReturn.addLink("team", uriInfo.getAbsolutePathBuilder().path("teams")
                    .path(String.valueOf(participant.getTeam().getId())).build().toString());
        }

        if (participant.getTournament() != null) {
            toReturn.addLink("tournament", uriInfo.getAbsolutePathBuilder().path("tournaments")
                    .path(String.valueOf(participant.getTournament().getId())).build().toString());
        }

        if (participant.getPfpId() != null) {
            toReturn.addLink("profilePicture", uriInfo.getAbsolutePathBuilder().path("images")
                    .path(String.valueOf(participant.getPfpId())).build().toString());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Integer getScoreDifference() {
        return scoreDifference;
    }

    public void setScoreDifference(Integer scoreDifference) {
        this.scoreDifference = scoreDifference;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    public boolean isHasRated() {
        return hasRated;
    }

    public void setHasRated(boolean hasRated) {
        this.hasRated = hasRated;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}