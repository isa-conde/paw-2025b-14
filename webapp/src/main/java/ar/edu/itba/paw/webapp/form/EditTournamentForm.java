package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.constraints.DatesConstraint;
import ar.edu.itba.paw.webapp.constraints.DatesNullabilityConstraint;
import ar.edu.itba.paw.webapp.constraints.MaxParticipantsNotBelowCurrent;
import ar.edu.itba.paw.webapp.constraints.MaxParticipantsNullabilityConstraint;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.time.LocalDate;

@DatesConstraint
@DatesNullabilityConstraint
@MaxParticipantsNotBelowCurrent
@MaxParticipantsNullabilityConstraint
public class EditTournamentForm implements HasDates{
    @NotNull
    private Long tournamentId;
    @NotBlank(message = "{createTournament.notNull}")
    @Size(max = 100)
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end_date;
    @Min(value = 4, message = "{createTournament.minParticipants}")
    @Max(value = 32, message = "{createTournament.maxParticipantsError}")
    private Integer max_participants;
    private MultipartFile image;
    private MultipartFile rules;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public Long getTournamentId(){ return tournamentId; }
    public void setTournamentId(Long id){ this.tournamentId = id; }

    public MultipartFile getRules() {
        return rules;
    }
    public void setRules(MultipartFile rules) {
        this.rules = rules;
    }
}
