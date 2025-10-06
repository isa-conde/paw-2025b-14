package ar.edu.itba.paw.webapp.form;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueTeamNameOnEditValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTeamNameOnEdit {

    String message() default "Team name is already taken";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
