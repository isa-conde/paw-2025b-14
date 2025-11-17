package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.EmailIsTaken;
import ar.edu.itba.paw.webapp.validation.PasswordMatches;
import ar.edu.itba.paw.webapp.validation.PasswordValidation;
import ar.edu.itba.paw.webapp.validation.UsernameIsTaken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

@PasswordMatches
public class UserForm implements HasPasswordMatcher {
	//id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
	//email text NOT NULL UNIQUE,
	//username text NOT NULL UNIQUE,
	//password text NOT NULL,
	//CONSTRAINT username_length CHECK (char_length(username) < 15)

	@UsernameIsTaken(message = "{error.registerForm.usernameUsed}")
	@NotBlank(message = "{form.requiredField}")
	@Size(max = 31)
	@Pattern( regexp = "|[a-zA-Z][-a-zA-Z0-9_]+" , message = "home.login.usernameFormat")
	private String username;
	
	@PasswordValidation
	@NotNull(message = "{form.requiredField}")
	private String password;

	@NotNull(message = "{form.requiredField}")
	private String repeatPassword;

	@EmailIsTaken(message = "{error.registerForm.emailUsed}")
	@Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Invalid email format")
	@NotNull(message = "{form.requiredField}")
	private String email;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
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
