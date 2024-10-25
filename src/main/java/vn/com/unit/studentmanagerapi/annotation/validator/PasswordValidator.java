package vn.com.unit.studentmanagerapi.annotation.validator;

import vn.com.unit.studentmanagerapi.annotation.constraint.PasswordConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }
        if (password.contains(" ")) {
            return false;
        }

        if (!password.isEmpty() && !password.contains(" ")) {
            return password.matches("[\\p{ASCII}]*");
        }
        return true;
    }
}
