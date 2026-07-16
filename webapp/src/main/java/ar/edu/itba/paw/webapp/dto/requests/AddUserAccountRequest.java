package ar.edu.itba.paw.webapp.dto.requests;

import ar.edu.itba.paw.model.enums.Platform;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddUserAccountRequest {

    @NotNull
    private Platform platform;

    @NotBlank
    @Size(max = 100)
    private String username;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
