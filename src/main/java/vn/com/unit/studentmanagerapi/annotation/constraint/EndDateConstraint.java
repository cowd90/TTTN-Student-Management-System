package vn.com.unit.studentmanagerapi.annotation.constraint;

import vn.com.unit.studentmanagerapi.annotation.validator.EndDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndDateValidator.class)
public @interface EndDateConstraint {
    String message() default "END_DATE_BEFORE_START_DATE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}