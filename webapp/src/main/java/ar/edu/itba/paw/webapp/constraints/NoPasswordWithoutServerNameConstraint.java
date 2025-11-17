package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoPasswordWithoutServerNameValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoPasswordWithoutServerNameConstraint {
    String message() default "error.tournamentForm.noServerName";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}