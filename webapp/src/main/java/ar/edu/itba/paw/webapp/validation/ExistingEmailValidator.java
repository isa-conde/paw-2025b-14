package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingEmailValidator implements ConstraintValidator<ExistingEmail, String> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(email == null) return true;
        return userService.findByEmail(email).isPresent();
    }
}
