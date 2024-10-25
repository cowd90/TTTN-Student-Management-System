package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.com.unit.studentmanagerapi.annotation.constant.StudentIDFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.SubjectIDFieldError;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationSubjectDeleteRequest {

    @NotNull(message = SubjectIDFieldError.NULL)
    @Min(value = 1, message = SubjectIDFieldError.NOT_POSITIVE)
    Long subjectID;

    @NotNull(message = StudentIDFieldError.NULL)
    @Min(value = 1, message = StudentIDFieldError.NOT_POSITIVE)
    Long studentID;

}
