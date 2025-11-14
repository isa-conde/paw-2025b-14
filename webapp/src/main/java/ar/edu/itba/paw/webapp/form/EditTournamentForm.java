package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.Constants;
import ar.edu.itba.paw.webapp.constraints.*;
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
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @Min(value = 4, message = "{createTournament.minParticipants}")
    @Max(value = 32, message = "{createTournament.maxParticipantsError}")
    private Integer maxParticipants;
    @ImageConstraint(maxSize = Constants.MAX_BANNER_SIZE)
    private MultipartFile image;
    @ValidPDF(maxSize = Constants.MAX_PDF_SIZE)
    private MultipartFile rules;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public Long getTournamentId(){ return tournamentId; }
    public void setTournamentId(Long id){ this.tournamentId = id; }

    public MultipartFile getRules() {
        return rules;
    }
    public void setRules(MultipartFile rules) {
        this.rules = rules;
    }
}
