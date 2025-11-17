package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UsernameIsTakenValidator.class)
public @interface UsernameIsTaken {

    String message() default "This username is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
