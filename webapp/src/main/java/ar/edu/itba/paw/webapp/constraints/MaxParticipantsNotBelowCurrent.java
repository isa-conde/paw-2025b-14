package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxParticipantsValidator.class)
public @interface MaxParticipantsNotBelowCurrent {
    String message() default "{tournament.maxParticipants.belowCurrent}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

