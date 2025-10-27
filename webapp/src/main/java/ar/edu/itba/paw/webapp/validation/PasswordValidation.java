package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = PasswordValidator.class)
public @interface  PasswordValidation {
    
    String message() default "Password must be 8-20 characters long and contain at least one lowercase letter, one uppercase letter, and one digit";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
