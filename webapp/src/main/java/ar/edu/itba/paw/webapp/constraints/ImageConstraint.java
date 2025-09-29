package ar.edu.itba.paw.webapp.constraints;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageConstraint {
    String message() default "error.tournamentForm.emptyImage";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
