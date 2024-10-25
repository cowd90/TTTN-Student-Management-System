package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import vn.com.unit.studentmanagerapi.annotation.constant.CourseIDFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.EndDateFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.NumberOfCreditFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.NumberOfStudentFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.StartDateFieldError;
import vn.com.unit.studentmanagerapi.annotation.constant.TuitionFieldError;
import vn.com.unit.studentmanagerapi.annotation.constraint.DescriptionConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.EndDateConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.NumberOfCreditConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.NumberOfStudentConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.SubjectNameConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.TuitionConstraint;
import vn.com.unit.studentmanagerapi.config.GlobalValue;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EndDateConstraint
public class SubjectCreateRequest implements DateRange{

    @SubjectNameConstraint
    String subjectName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = StartDateFieldError.NULL)
    @Future(message = StartDateFieldError.BEFORE_CURRENT_DATE)
    LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = EndDateFieldError.NULL)
    LocalDate endDate;

    @NumberOfStudentConstraint
    Integer numberOfStudent;

    @NumberOfCreditConstraint
    Integer numberOfCredit;

    @NotNull(message = CourseIDFieldError.NULL)
    CourseID courseID;

    @TuitionConstraint
    Long tuition;

    @DescriptionConstraint
    String description;

}
