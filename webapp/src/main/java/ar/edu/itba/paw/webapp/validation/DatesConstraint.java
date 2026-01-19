package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DatesValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DatesConstraint {
    String message() default "error.tournamentForm.invalidDates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
