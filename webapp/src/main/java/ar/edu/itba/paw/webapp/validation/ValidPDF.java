package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.Constants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PDFValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPDF {
    String message() default "Archivo PDF inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSize() default Constants.MAX_PDF_SIZE;
    boolean optional() default true;
}