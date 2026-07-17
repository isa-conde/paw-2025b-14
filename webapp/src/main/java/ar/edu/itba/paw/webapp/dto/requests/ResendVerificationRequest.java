package ar.edu.itba.paw.webapp.dto.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ResendVerificationRequest {

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Invalid email format")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
