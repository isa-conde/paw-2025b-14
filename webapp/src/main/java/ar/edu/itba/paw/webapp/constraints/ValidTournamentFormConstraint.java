package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TournamentFormValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public  @interface ValidTournamentFormConstraint {
    String message() default "error.tournamentForm.invalidDates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


