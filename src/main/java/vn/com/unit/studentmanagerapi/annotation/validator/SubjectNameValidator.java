package vn.com.unit.studentmanagerapi.annotation.validator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.unit.studentmanagerapi.annotation.constraint.SubjectNameConstraint;
import vn.com.unit.studentmanagerapi.annotation.constant.SubjectNameFieldError;
import vn.com.unit.studentmanagerapi.config.GlobalValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectNameValidator implements ConstraintValidator<SubjectNameConstraint, String> {
    @Value("${student-manager.subject.validation.subject-name.min}")
    int min;
    @Value("${student-manager.subject.validation.subject-name.max}")
    int max;

    @Override
    public boolean isValid(String subjectName, ConstraintValidatorContext context) {
        return Stream.of(
            new ValidationCheck(subjectName == null || subjectName.trim().isEmpty(), SubjectNameFieldError.IS_EMPTY),
            new ValidationCheck(subjectName != null && subjectName.trim().length() < min, SubjectNameFieldError.TOO_SHORT),
            new ValidationCheck(subjectName != null && subjectName.trim().length() > max, SubjectNameFieldError.TOO_LONG),
            new ValidationCheck(subjectName != null && subjectName.trim().matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*"), SubjectNameFieldError.CONTAINING_SPECIAL_CHARACTERS)
        )
                .filter(validationCheck -> validationCheck.hasError)
                .findFirst()
                .map(check -> {
                    setValidationMessage(context, check.message);
                    return false;
                })
                .orElse(true);
    }

    private void setValidationMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class ValidationCheck{
        boolean hasError;
        String message;
    }

}
