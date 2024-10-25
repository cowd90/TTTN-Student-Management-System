package vn.com.unit.studentmanagerapi.annotation.validator;

import vn.com.unit.studentmanagerapi.annotation.constant.EndDateFieldError;
import vn.com.unit.studentmanagerapi.annotation.constraint.EndDateConstraint;
import vn.com.unit.studentmanagerapi.dto.request.DateRange;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class EndDateValidator implements ConstraintValidator<EndDateConstraint, DateRange> {
    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {
        if (dateRange.getStartDate() == null || dateRange.getEndDate() == null) {
            return true; // @NotNull
        }
        boolean after = dateRange.getEndDate().isAfter(dateRange.getStartDate());
        if(!after){
            setValidationMessage(context, EndDateFieldError.BEFORE_START_DATE);
            return false;
        }
        return true;
    }


    private void setValidationMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
