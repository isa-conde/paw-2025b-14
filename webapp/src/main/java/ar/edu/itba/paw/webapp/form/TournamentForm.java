package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.constraints.DatesConstraint;
import ar.edu.itba.paw.webapp.constraints.ImageConstraint;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import javax.validation.constraints.*;

@DatesConstraint(groups = TournamentForm.StepOne.class)
public class TournamentForm implements HasDates{

    public interface StepOne {}
    public interface StepTwo {}

    @NotBlank(message = "{home.createTournament.notNull}", groups = StepOne.class)
    @Size(max = 100, groups = StepOne.class)
    private String name;
    @NotNull(message = "{home.createTournament.notNull}", groups = StepOne.class)
    private Long game_id;
    @NotNull(message = "{home.createTournament.notNull}", groups = StepOne.class)
    private Region region;
    @NotNull(message = "{home.createTournament.notNull}", groups = StepTwo.class)
    private Elo elo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{home.createTournament.notNull}", groups = StepOne.class)
    private LocalDate start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{home.createTournament.notNull}", groups = StepOne.class)
    private LocalDate end_date;
    @NotNull(message = "{home.createTournament.notNull}", groups = StepOne.class)
    private Structure structure;
    @NotNull(message = "{home.createTournament.notNull}", groups = StepTwo.class)
    @Min(value = 4, message = "{home.createTournament.minParticipants}", groups = StepTwo.class)
    @Max(value = 32, message = "{home.createTournament.maxParticipantsError}", groups = StepTwo.class)
    private Integer max_participants;
    @ImageConstraint(groups = StepTwo.class)
    @NotNull(message = "{home.createTournament.notNull}", groups = StepTwo.class)
    private MultipartFile image;
    @NotNull(message = "{home.createTournament.notNull}", groups = StepTwo.class)
    private Long format_id;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getGame_id() {
        return game_id;
    }
    public void setGame_id(Long game_id) {
        this.game_id = game_id;
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

    public LocalDate getStart_date() {
        return start_date;
    }
    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }
    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public Structure getStructure() {
        return structure;
    }
    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Integer getMax_participants() {
        return max_participants;
    }
    public void setMax_participants(Integer max_participants) {
        this.max_participants = max_participants;
    }

    public MultipartFile getImage() {
        return image;
    }
    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Long getFormat_id() {
        return format_id;
    }
    public void setFormat_id(Long format_id) {
        this.format_id = format_id;
    }
}

