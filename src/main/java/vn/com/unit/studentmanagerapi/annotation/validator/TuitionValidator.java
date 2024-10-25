package vn.com.unit.studentmanagerapi.annotation.validator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.unit.studentmanagerapi.annotation.constant.NumberOfCreditFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.TuitionFieldError;
import vn.com.unit.studentmanagerapi.annotation.constraint.NumberOfStudentConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.TuitionConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuitionValidator implements ConstraintValidator<TuitionConstraint, Long> {
    @Value("${student-manager.subject.validation.tuition.min}")
    int min;
    @Value("${student-manager.subject.validation.tuition.max}")
    int max;

    @Override
    public boolean isValid(Long tuition, ConstraintValidatorContext context) {
        return Stream.of(
            new ValidationCheck(tuition == null, TuitionFieldError.NULL),
            new ValidationCheck(tuition != null && tuition < min, TuitionFieldError.SMALL),
            new ValidationCheck(tuition != null && tuition > max, TuitionFieldError.BIG)
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
