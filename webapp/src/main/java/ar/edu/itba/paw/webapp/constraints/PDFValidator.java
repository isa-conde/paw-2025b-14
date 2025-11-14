package ar.edu.itba.paw.webapp.constraints;

import org.springframework.web.multipart.MultipartFile;
import ar.edu.itba.paw.webapp.constraints.ValidPDF;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PDFValidator implements ConstraintValidator<ValidPDF, MultipartFile> {

    private long maxSize;
    private boolean optional;

    @Override
    public void initialize(ValidPDF constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return optional;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.invalidPdf}")
                    .addConstraintViolation();
            return false;
        }

        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.invalidPdf.maxsize}")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
