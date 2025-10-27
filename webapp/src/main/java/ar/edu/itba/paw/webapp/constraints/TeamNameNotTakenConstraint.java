package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TeamNameNotTakenValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public  @interface TeamNameNotTakenConstraint {
    String message() default "Team name is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Documented
    @Constraint(validatedBy = MembersCountValidator.UniqueTeamNameOnEditValidator.class)
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface UniqueTeamNameOnEdit {

        String message() default "Team name is already taken";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
