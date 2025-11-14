package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.interfaces.Constants;
import ar.edu.itba.paw.webapp.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.constraints.UpdateUsernameTakenConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@UpdateUsernameTakenConstraint
public class EditProfileForm {


    @NotNull
    private Long userId;
    @NotBlank(message = "{createTournament.notNull}")
    @Size(max = Constants.MAX_NAME_SIZE)
    private String username;
    @Size(max = Constants.MAX_BIO_SIZE)
    private String bio;

    @ImageConstraint(maxSize = Constants.MAX_PFP_SIZE)
    private MultipartFile profilePicture;
    @ImageConstraint(maxSize = Constants.MAX_BANNER_SIZE)
    private MultipartFile bannerPicture;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Long getUserId(){ return userId; }
    public void setUserId(Long id){ this.userId = id; }

    public MultipartFile getBannerPicture() {
        return bannerPicture;
    }
    public void setBannerPicture(MultipartFile bannerPicture) {
        this.bannerPicture = bannerPicture;
    }

    public @Size(max = Constants.MAX_BIO_SIZE) String getBio() {
        return bio;
    }
    public void setBio(@Size(max = Constants.MAX_BIO_SIZE) String bio) {
        this.bio = bio;
    }
}
