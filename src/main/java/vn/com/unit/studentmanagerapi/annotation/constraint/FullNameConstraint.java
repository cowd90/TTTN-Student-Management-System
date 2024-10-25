package vn.com.unit.studentmanagerapi.annotation.constraint;

import vn.com.unit.studentmanagerapi.annotation.validator.FullNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FullNameValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FullNameConstraint {
    String message() default "Invalid string";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
