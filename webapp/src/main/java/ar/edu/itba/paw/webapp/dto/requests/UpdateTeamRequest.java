package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class UpdateTeamRequest {

    @NotBlank
    private String name;

    private String profilePictureBase64;
    private String bannerBase64;
    private List<String> members;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureBase64() {
        return profilePictureBase64;
    }

    public void setProfilePictureBase64(String profilePictureBase64) {
        this.profilePictureBase64 = profilePictureBase64;
    }

    public String getBannerBase64() {
        return bannerBase64;
    }

    public void setBannerBase64(String bannerBase64) {
        this.bannerBase64 = bannerBase64;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
