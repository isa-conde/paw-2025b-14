package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.webapp.constraints.ValidTournamentFormConstraint;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import javax.validation.constraints.*;

@ValidTournamentFormConstraint
public class TournamentForm {

    @NotBlank(message = "{home.createTournament.notNull}")
    @Size(max = 100)
    private String name;
    @NotNull(message = "{home.createTournament.notNull}")
    private Long game_id;
    @NotNull(message = "{home.createTournament.notNull}")
    private Region region;
    @NotNull(message = "{home.createTournament.notNull}")
    private Elo elo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{home.createTournament.notNull}")
    private LocalDate start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{home.createTournament.notNull}")
    private LocalDate end_date;
    @Size(max = 255, message = "Format must be at most 255 characters long")
    @NotBlank(message = "{home.createTournament.notNull}")
    private String format;
    @NotNull(message = "{home.createTournament.notNull}")
    private Structure structure;
    @NotNull(message = "{home.createTournament.notNull}")
    @Min(value = 2, message = "{home.createTournament.minParticipants}")
    @Max(value = 32, message = "{home.createTournament.maxParticipantsError}")
    private Integer max_participants;
    @NotNull(message = "{home.createTournament.notNull}")
    private MultipartFile image;

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

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
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
}
