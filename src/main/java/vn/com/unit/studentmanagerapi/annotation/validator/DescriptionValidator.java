package vn.com.unit.studentmanagerapi.annotation.validator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import vn.com.unit.studentmanagerapi.annotation.constant.DescriptionFieldError;
import vn.com.unit.studentmanagerapi.annotation.constraint.DescriptionConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DescriptionValidator implements ConstraintValidator<DescriptionConstraint, String> {

    @Override
    public boolean isValid(String description, ConstraintValidatorContext context) {
        return Stream.of(
            new ValidationCheck(description != null && description.trim().trim().isEmpty(), DescriptionFieldError.EMPTY)
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
