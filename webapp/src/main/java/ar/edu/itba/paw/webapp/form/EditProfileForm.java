package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.UsernameIsTaken;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class EditProfileForm {


    @NotNull
    private Long userId;
    @NotBlank(message = "{home.createTournament.notNull}")
    @Size(max = 100)
    @UsernameIsTaken(message = "{error.registerForm.usernameUsed}")
    private String username;
    @Size(max = 255)
    private String bio;

    private MultipartFile profilePicture;

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

    public @Size(max = 255) String getBio() {
        return bio;
    }
    public void setBio(@Size(max = 255) String bio) {
        this.bio = bio;
    }
}
