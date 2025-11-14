package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.constraints.DatesConstraint;
import ar.edu.itba.paw.webapp.constraints.DiscordUrlConstraint;
import ar.edu.itba.paw.webapp.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.constraints.NoPasswordWithoutServerNameConstraint;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import javax.validation.constraints.*;

@DatesConstraint(groups = TournamentForm.StepOne.class)
@NoPasswordWithoutServerNameConstraint(groups = TournamentForm.StepThree.class)
public class TournamentForm implements HasDates, HasServer{

    public interface StepOne {}
    public interface StepTwo {}
    public interface StepThree {}

    @NotBlank(message = "{createTournament.notNull}", groups = StepOne.class)
    @Size(max = 100, groups = StepOne.class)
    private String name;
    @NotNull(message = "{createTournament.notNull}", groups = StepOne.class)
    private Long gameId;
    @NotNull(message = "{createTournament.notNull}", groups = StepOne.class)
    private Region region;
    @NotNull(message = "{createTournament.notNull}", groups = StepTwo.class)
    private Elo elo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{createTournament.notNull}", groups = StepOne.class)
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{createTournament.notNull}", groups = StepOne.class)
    private LocalDate endDate;
    @NotNull(message = "{createTournament.notNull}", groups = StepOne.class)
    private Structure structure;
    @NotNull(message = "{createTournament.notNull}", groups = StepTwo.class)
    @Min(value = 4, message = "{createTournament.minParticipants}", groups = StepTwo.class)
    @Max(value = 32, message = "{createTournament.maxParticipantsError}", groups = StepTwo.class)
    private Integer maxParticipants;
    @ImageConstraint(groups = StepTwo.class)
    @NotNull(message = "{createTournament.notNull}", groups = StepTwo.class)
    private MultipartFile image;
    @NotNull(message = "{createTournament.notNull}", groups = StepTwo.class)
    private Long formatId;
    private String serverName;
    private String serverPassword;
    @DiscordUrlConstraint(groups = StepThree.class)
    private String discordChannel;
    byte[] imageBytes;

    private MultipartFile rules;

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

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
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

    public MultipartFile getImage() {
        return image;
    }
    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Long getFormatId() {
        return formatId;
    }
    public void setFormatId(Long formatId) {
        this.formatId = formatId;
    }

    public MultipartFile getRules() {
        return rules;
    }
    public void setRules(MultipartFile rules) {
        this.rules = rules;
    }

    @Override
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
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

    public byte[] getImageBytes() {
        return imageBytes;
    }
    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}

