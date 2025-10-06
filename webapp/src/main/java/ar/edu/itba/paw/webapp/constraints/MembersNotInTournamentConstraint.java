package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MembersNotInTournamentValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MembersNotInTournamentConstraint {
    String message() default "{members.already_in_tournament}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

