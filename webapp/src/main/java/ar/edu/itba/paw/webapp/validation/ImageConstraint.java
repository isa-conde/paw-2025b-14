package ar.edu.itba.paw.webapp.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageConstraint {
    String message() default "error.tournamentForm.invalidImage";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSize() default 2 * 1024 * 1024;
    String[] allowedExtensions() default { "jpg", "jpeg", "png" };
    boolean optional() default true;
}
