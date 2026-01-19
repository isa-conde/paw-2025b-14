package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.ExistingUsersContraint;
import ar.edu.itba.paw.webapp.validation.ImageConstraint;
import ar.edu.itba.paw.webapp.validation.TeamNameNotTakenConstraint;
import org.springframework.web.multipart.MultipartFile;
import ar.edu.itba.paw.interfaces.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateTeamForm {

    @NotBlank(message = "{createTournament.notNull}")
    @Size(max = 100)
    @TeamNameNotTakenConstraint(message = "{team.create.error.nameTaken}")
    private String name;
    @ImageConstraint(maxSize = Constants.MAX_PFP_SIZE)
    private MultipartFile banner;
    @ImageConstraint(maxSize = Constants.MAX_PFP_SIZE)
    private MultipartFile pfp;
    private Long ownerId;
    @ExistingUsersContraint
    private List<String> members;

    private String returnUrl;

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

    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getMembers() {
        return members;
    }
    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getReturnUrl() {
        return returnUrl;
    }
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
