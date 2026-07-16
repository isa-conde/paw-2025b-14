package ar.edu.itba.paw.webapp.dto.requests;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class CreateTournamentRequest {

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Long gameId;

    @NotNull
    private Region region;

    @NotNull
    private Elo elo;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Structure structure;

    @NotNull
    @Positive
    private Integer maxParticipants;

    @NotNull
    @Positive
    private Long formatId;

    private String imageBase64;
    private String rulesBase64;
    private String serverName;
    private String serverPassword;
    private String discordChannel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Elo getElo() {
        return elo;
    }

    public void setElo(Elo elo) {
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

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Long getFormatId() {
        return formatId;
    }

    public void setFormatId(Long formatId) {
        this.formatId = formatId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getRulesBase64() {
        return rulesBase64;
    }

    public void setRulesBase64(String rulesBase64) {
        this.rulesBase64 = rulesBase64;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getDiscordChannel() {
        return discordChannel;
    }

    public void setDiscordChannel(String discordChannel) {
        this.discordChannel = discordChannel;
    }
}
