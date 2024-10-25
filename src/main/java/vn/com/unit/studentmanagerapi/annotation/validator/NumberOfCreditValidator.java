package vn.com.unit.studentmanagerapi.annotation.validator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.unit.studentmanagerapi.annotation.constant.NumberOfCreditFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.NumberOfStudentFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.TuitionFieldError;
import vn.com.unit.studentmanagerapi.annotation.constraint.NumberOfCreditConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.NumberOfStudentConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NumberOfCreditValidator implements ConstraintValidator<NumberOfCreditConstraint, Integer> {
    @Value("${student-manager.subject.validation.number-of-credit.min}")
    int min;
    @Value("${student-manager.subject.validation.number-of-credit.max}")
    int max;

    @Override
    public boolean isValid(Integer numberOfCredit, ConstraintValidatorContext context) {
        return Stream.of(
            new ValidationCheck(numberOfCredit == null, NumberOfCreditFieldError.NULL),
            new ValidationCheck(numberOfCredit != null && numberOfCredit < min, NumberOfCreditFieldError.POSITIVE_INT),
            new ValidationCheck(numberOfCredit != null && numberOfCredit > max, NumberOfCreditFieldError.EXCEEDS_LIMIT)
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
