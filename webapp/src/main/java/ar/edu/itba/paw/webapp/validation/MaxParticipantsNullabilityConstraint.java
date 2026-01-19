package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MaxParticipantsNullabilityValidator.class)
public @interface MaxParticipantsNullabilityConstraint {
    String message() default "{tournament.maxParticipants.nullability}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}