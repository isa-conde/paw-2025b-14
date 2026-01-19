package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ExistingUsersValidator implements ConstraintValidator<ExistingUsersContraint, List<String>> {

    @Autowired
    private UserService us;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(ExistingUsersContraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (value == null || value.isEmpty()){
            return true;
        }
        List<String> invalidUsers = new ArrayList<>();
        for (String s: value){
            if (!us.usernameIsTaken(s)){
                isValid = false;
                invalidUsers.add(s);

                context.disableDefaultConstraintViolation();
                String msg = messageSource.getMessage(
                        "team.create.error.invalidUser",
                        new Object[]{s},
                        LocaleContextHolder.getLocale()
                );

                context.buildConstraintViolationWithTemplate(msg)
                        .addConstraintViolation();
            }
        }

        value.removeAll(invalidUsers);

        return isValid;
    }
}
