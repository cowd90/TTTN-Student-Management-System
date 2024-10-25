package vn.com.unit.studentmanagerapi.annotation.constraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.unit.studentmanagerapi.annotation.validator.SubjectNameValidator;
import vn.com.unit.studentmanagerapi.config.GlobalValue;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SubjectNameValidator.class)
public @interface SubjectNameConstraint {
    int min() default 0;
    int max() default 0;
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
