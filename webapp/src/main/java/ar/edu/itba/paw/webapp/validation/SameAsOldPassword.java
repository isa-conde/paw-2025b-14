package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = SameAsOldPasswordValidator.class)
public @interface SameAsOldPassword {

    String message() default "{passwordReset.error.sameAsOld}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
