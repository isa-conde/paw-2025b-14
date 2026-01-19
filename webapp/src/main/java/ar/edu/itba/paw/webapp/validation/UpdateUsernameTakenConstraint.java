package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE) // 👈 nivel de clase
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateUsernameTakenValidator.class)
@Documented
public @interface  UpdateUsernameTakenConstraint {
    String message() default "{error.registerForm.usernameUsed}"; // clave i18n o mensaje fijo
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
