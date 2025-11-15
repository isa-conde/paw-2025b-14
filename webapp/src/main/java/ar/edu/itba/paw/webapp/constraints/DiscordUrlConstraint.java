package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DiscordUrlValidator.class)
@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordUrlConstraint {
    String message() default "error.tournamentForm.notADiscordLink";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
