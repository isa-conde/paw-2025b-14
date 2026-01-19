package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.EditProfileForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UpdateUsernameTakenValidator implements ConstraintValidator<UpdateUsernameTakenConstraint, EditProfileForm> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UpdateUsernameTakenConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EditProfileForm form, ConstraintValidatorContext context) {
        if (form == null) return true;

        String username = form.getUsername();
        Long userId = form.getUserId();

        if (username == null || username.isBlank() || userId == null) return true;

        Optional<User> currentUserOpt = userService.findById(userId);
        if (currentUserOpt.isEmpty()) return true;

        User currentUser = currentUserOpt.get();

        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            return true;
        }

        boolean taken = userService.usernameIsTaken(username);
        if (taken) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "{error.registerForm.usernameUsed}"
                    ).addPropertyNode("username") // 👈 apunta al campo username
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
