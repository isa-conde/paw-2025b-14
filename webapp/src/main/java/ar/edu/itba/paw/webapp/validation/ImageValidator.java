package ar.edu.itba.paw.webapp.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ImageValidator implements ConstraintValidator<ImageConstraint, MultipartFile> {

    private long maxSize;
    private String[] allowedExtensions;
    private boolean optional;

    @Override
    public void initialize(ImageConstraint constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedExtensions = constraintAnnotation.allowedExtensions();
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            if (optional) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{error.tournamentForm.emptyImage}")
                        .addConstraintViolation();
                return false;
            }
        }
        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "{error.invalidImage.maxsize}")
                    .addConstraintViolation();
            return false;
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.tournamentForm.invalidImage}")
                    .addConstraintViolation();
            return false;
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        boolean validExtension = Arrays.asList(allowedExtensions).contains(extension);

        if (!validExtension) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "{error.tournamentForm.invalidImage}")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
