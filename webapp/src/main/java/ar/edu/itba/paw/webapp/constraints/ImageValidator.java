package ar.edu.itba.paw.webapp.constraints;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class ImageValidator implements ConstraintValidator<ImageConstraint, MultipartFile> {

    @Override
    public void initialize(ImageConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value.isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.emptyImage}")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
