package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.constraints.ExistingUsersContraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class EditTeamForm {

    @NotNull
    private Long teamId;
    @NotBlank(message = "{home.createTournament.notNull}")
    @Size(max = 100)
    private String name;
    @ExistingUsersContraint
    List<String> members;

    private MultipartFile profilePicture;

    private MultipartFile bannerPicture;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Long getTeamId(){ return teamId; }
    public void setTeamId(Long id){ this.teamId = id; }

    public MultipartFile getBannerPicture() {
        return bannerPicture;
    }
    public void setBannerPicture(MultipartFile bannerPicture) {
        this.bannerPicture = bannerPicture;
    }

    public List<String> getMembers() {
        return members;
    }
    public void setMembers(List<String> members) {
        this.members = members;
    }
}
