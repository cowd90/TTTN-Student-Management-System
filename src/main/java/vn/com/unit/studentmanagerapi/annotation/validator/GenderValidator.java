package vn.com.unit.studentmanagerapi.annotation.validator;

import vn.com.unit.studentmanagerapi.annotation.constraint.GenderConstraint;
import vn.com.unit.studentmanagerapi.entity.enums.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class GenderValidator implements ConstraintValidator<GenderConstraint, Gender> {

    private final List<String> acceptedGenders = Arrays.asList("MALE", "FEMALE", "OTHER");

    @Override
    public void initialize(GenderConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return acceptedGenders.contains(value.name().toUpperCase());
    }
}
