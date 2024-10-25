package vn.com.unit.studentmanagerapi.annotation.validator;

import vn.com.unit.studentmanagerapi.annotation.constraint.FullNameConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FullNameValidator implements ConstraintValidator<FullNameConstraint, String> {
    @Override
    public void initialize(FullNameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name != null && !name.isEmpty()) {
            return name.matches("[\\p{L} ]+");
        }
        return true;
    }
}
