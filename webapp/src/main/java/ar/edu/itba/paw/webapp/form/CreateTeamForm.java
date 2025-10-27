package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.constraints.ExistingUsersContraint;
import ar.edu.itba.paw.webapp.constraints.TeamNameNotTakenConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateTeamForm {

    @NotBlank(message = "{home.createTournament.notNull}")
    @Size(max = 100)
    @TeamNameNotTakenConstraint(message = "{team.create.error.nameTaken}")
    private String name;
    private MultipartFile banner;
    private MultipartFile pfp;
    private Long owner_id;
    @ExistingUsersContraint
    private List<String> members;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getBanner() {
        return banner;
    }
    public void setBanner(MultipartFile banner) {
        this.banner = banner;
    }

    public MultipartFile getPfp() {
        return pfp;
    }
    public void setPfp(MultipartFile pfp) {
        this.pfp = pfp;
    }

    public Long getOwner_id() {
        return owner_id;
    }
    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
    }

    public List<String> getMembers() {
        return members;
    }
    public void setMembers(List<String> members) {
        this.members = members;
    }

}
