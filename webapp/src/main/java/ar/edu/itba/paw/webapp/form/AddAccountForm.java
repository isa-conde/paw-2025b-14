package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.enums.Platform;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddAccountForm {

    @NotBlank
    @Size(max = 100)
    String username;

    @NotNull
    Platform platform;

    @NotNull
    long userId;

    public AddAccountForm(){}

    public @NotNull String getUsername() {
        return username;
    }
    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public @NotNull Platform getPlatform() {
        return platform;
    }
    public void setPlatform(@NotNull Platform platform) {
        this.platform = platform;
    }

    @NotNull
    public long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull long userId) {
        this.userId = userId;
    }
}
