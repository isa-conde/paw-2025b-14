package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class EmailForm {

    @ExistingEmail
    @NotNull(message = "Please enter your email.")
    @Email(message = "Invalid email format.")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
