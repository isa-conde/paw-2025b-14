package ar.edu.itba.paw.webapp.constraints;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistingUsersValidator.class)
@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingUsersContraint {
    String message() default "error.createTeam.invalidUser";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


