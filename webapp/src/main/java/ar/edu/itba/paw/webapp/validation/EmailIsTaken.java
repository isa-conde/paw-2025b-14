package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = EmailIsTakenValidator.class)
public @interface EmailIsTaken {

    String message() default "This email is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}