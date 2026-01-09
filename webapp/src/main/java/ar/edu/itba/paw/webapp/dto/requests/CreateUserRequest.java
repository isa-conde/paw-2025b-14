package ar.edu.itba.paw.webapp.dto.requests;

import ar.edu.itba.paw.webapp.form.HasPasswordMatcher;
import ar.edu.itba.paw.webapp.validation.EmailIsTaken;
import ar.edu.itba.paw.webapp.validation.PasswordMatches;
import ar.edu.itba.paw.webapp.validation.PasswordValidation;
import ar.edu.itba.paw.webapp.validation.UsernameIsTaken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

@PasswordMatches
public class CreateUserRequest implements HasPasswordMatcher {

    private static final int MAX_USERNAME_SIZE = 31;

    @NotBlank(message = "{form.requiredField}")
    @UsernameIsTaken(message = "{error.registerForm.usernameUsed}")
    @Size(max = MAX_USERNAME_SIZE)
    @Pattern( regexp = "|[a-zA-Z][-a-zA-Z0-9_]+" , message = "home.login.usernameFormat")
    private String username;

    @PasswordValidation
    @NotBlank(message = "{form.requiredField}")
    private String password;

    @NotBlank(message = "{form.requiredField}")
    private String repeatPassword;

    @EmailIsTaken(message = "{error.registerForm.emailUsed}")
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Invalid email format")
    @NotBlank(message = "{form.requiredField}")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
