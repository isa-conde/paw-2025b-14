package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NoTieOnEliminationValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface NoTieOnEliminationConstraint {
    String message() default "{setMatchResultsForm.noTieOnEliminationConstraint}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}