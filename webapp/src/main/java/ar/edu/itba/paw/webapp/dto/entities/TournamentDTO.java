package ar.edu.itba.paw.webapp.dto.entities;

import ar.edu.itba.paw.model.Tournament;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TournamentDTO {

    private long id;
    private String name;
    private String region;
    private String elo;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format;
    private String structure;
    private int maxParticipants;
    private boolean openInscriptions;
    private boolean finished;
    private boolean groupStage;
    private boolean tournamentStarted;
    private Float rating;
    private String serverName;
    private String discordChannel;

    private List<LinkDTO> links = new ArrayList<>();

    public static Function<Tournament, TournamentDTO> mapper(final UriInfo uriInfo) {
        return (t) -> fromTournament(uriInfo, t);
    }

    public static TournamentDTO fromTournament(final UriInfo uriInfo, final Tournament tournament) {
        TournamentDTO toReturn = new TournamentDTO();

        toReturn.id = tournament.getId();
        toReturn.name = tournament.getName();
        toReturn.region = tournament.getRegion() == null ? null : tournament.getRegion().name();
        toReturn.elo = tournament.getElo() == null ? null : tournament.getElo().name();
        toReturn.startDate = tournament.getStartDate();
        toReturn.endDate = tournament.getEndDate();
        toReturn.format = tournament.getFormat();
        toReturn.structure = tournament.getStructure() == null ? null : tournament.getStructure().name();
        toReturn.maxParticipants = tournament.getMaxParticipants();
        toReturn.openInscriptions = Boolean.TRUE.equals(tournament.getOpenInscriptions());
        toReturn.finished = Boolean.TRUE.equals(tournament.getFinished());
        toReturn.groupStage = Boolean.TRUE.equals(tournament.getIsGroupStage());
        toReturn.tournamentStarted = tournament.getTournamentStarted();
        toReturn.rating = tournament.getRating();
        toReturn.serverName = tournament.getServerName();
        toReturn.discordChannel = tournament.getDiscordChannel();

        toReturn.addLink("self", uriInfo.getAbsolutePathBuilder().path("tournaments")
                .path(String.valueOf(tournament.getId())).build().toString());
        toReturn.addLink("creator", uriInfo.getAbsolutePathBuilder().path("users")
                .path(String.valueOf(tournament.getCreatorId())).build().toString());
        toReturn.addLink("game", uriInfo.getAbsolutePathBuilder().path("games")
                .path(String.valueOf(tournament.getGameId())).build().toString());
        toReturn.addLink("participants", uriInfo.getAbsolutePathBuilder().path("tournaments")
                .path(String.valueOf(tournament.getId()))
                .path("participants").build().toString());
        toReturn.addLink("matches", uriInfo.getAbsolutePathBuilder().path("tournaments")
                .path(String.valueOf(tournament.getId()))
                .path("matches").build().toString());
        toReturn.addLink("rules", uriInfo.getAbsolutePathBuilder().path("tournaments")
                .path(String.valueOf(tournament.getId()))
                .path("rules").build().toString());

        if (tournament.getFormatId() != null) {
            toReturn.addLink("format", uriInfo.getAbsolutePathBuilder().path("games")
                    .path(String.valueOf(tournament.getGameId()))
                    .path("formats")
                    .path(String.valueOf(tournament.getFormatId()))
                    .build().toString());
        }

        if (tournament.getImageId() != null) {
            toReturn.addLink("image", uriInfo.getAbsolutePathBuilder().path("images")
                    .path(String.valueOf(tournament.getImageId())).build().toString());
        }

        if (tournament.getTournamentWinner() != null) {
            toReturn.addLink("winner", uriInfo.getAbsolutePathBuilder().path("participants")
                    .path(String.valueOf(tournament.getTournamentWinner())).build().toString());
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getElo() {
        return elo;
    }

    public void setElo(String elo) {
        this.elo = elo;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean isOpenInscriptions() {
        return openInscriptions;
    }

    public void setOpenInscriptions(boolean openInscriptions) {
        this.openInscriptions = openInscriptions;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isGroupStage() {
        return groupStage;
    }

    public void setGroupStage(boolean groupStage) {
        this.groupStage = groupStage;
    }

    public boolean isTournamentStarted() {
        return tournamentStarted;
    }

    public void setTournamentStarted(boolean tournamentStarted) {
        this.tournamentStarted = tournamentStarted;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDiscordChannel() {
        return discordChannel;
    }

    public void setDiscordChannel(String discordChannel) {
        this.discordChannel = discordChannel;
    }

    public List<LinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDTO> links) {
        this.links = links;
    }
}