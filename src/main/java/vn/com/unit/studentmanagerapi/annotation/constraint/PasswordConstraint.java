package vn.com.unit.studentmanagerapi.annotation.constraint;

import vn.com.unit.studentmanagerapi.annotation.validator.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordConstraint {
    String message() default "Password should not contain accented characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
