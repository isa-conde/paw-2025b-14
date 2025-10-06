package ar.edu.itba.paw.webapp.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MembersBelongToTeamValidator.class)
public @interface MembersBelongToTeamConstraint {
    String message() default "{team.selection.not_in_team}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

