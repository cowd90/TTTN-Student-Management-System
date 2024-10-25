package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import vn.com.unit.studentmanagerapi.annotation.constraint.FullNameConstraint;
import vn.com.unit.studentmanagerapi.annotation.constraint.PasswordConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentCreateRequest {
    @NotEmpty(message = "FULL_NAME_NULL_EMPTY")
    @FullNameConstraint(message = "FULL_NAME_INVALID")
    @Size(max = 100, message = "FULL_NAME_TOO_LONG")
    String fullName;

    @NotEmpty(message = "EMAIL_NULL_EMPTY")
    @Email(message = "EMAIL_INVALID_FORMAT")
    @Size(max = 70, message = "EMAIL_TOO_LONG")
    String email;

    @Size(min = 8, max = 100, message = "PASSWORD_LENGTH_INVALID")
    @PasswordConstraint(message = "PASSWORD_INVALID")
    String password;

    @NotNull(message = "DOB_NULL")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;

    @NotNull(message = "GENDER_NULL")
    String gender;

    @NotNull(message = "DOA_NULL")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfAdmission;

    LocalDateTime createdAt;

    LocalDateTime updateAt;

    String updateBy;

}
