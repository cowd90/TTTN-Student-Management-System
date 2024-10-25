package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Valid
public class StudentUpdateRequest {
    @NotBlank(message = "FULL_NAME_NULL_EMPTY")
    @Size(max = 100, message = "FULL_NAME_TOO_LONG")
    String fullName;

    @Past(message = "DOB_IN_FUTURE")
    LocalDate dateOfBirth;

    @NotBlank(message = "GENDER_INVALID")
    String gender;

    LocalDateTime updateAt;

    String updateBy;

    boolean isDeleted;
}
