package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

public class UserForm {
	//id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
	//email text NOT NULL UNIQUE,
	//username text NOT NULL UNIQUE,
	//password text NOT NULL,
	//CONSTRAINT username_length CHECK (char_length(username) < 15)

	@Size(min = 6, max = 31)
	@Pattern( regexp = "[a-zA-Z][-a-zA-Z0-9_]+")
	@NotNull
	private String username;
	
//	@Size(min = 8, max =31)
//	private String password;
//
//	@Size(min = 8, max =31)
//	private String repeatPassword;
	
	@Pattern( regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
	@NotNull
	private String email;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
//	public String getPassword() {
//		return password;
//	}
//	public void setPassword(String password) {
//		this.password = password;
//	}
//	public String getRepeatPassword() {
//		return repeatPassword;
//	}
//	public void setRepeatPassword(String repeatPassword) {
//		this.repeatPassword = repeatPassword;
//	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
