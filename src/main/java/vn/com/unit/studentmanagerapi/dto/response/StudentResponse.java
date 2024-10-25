package vn.com.unit.studentmanagerapi.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class StudentResponse {
    Long studentID;
    String fullName;
    String avatar;
    String email;
    LocalDate dateOfBirth;
    String gender;
    LocalDate dateOfAdmission;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
    String updateBy;
    boolean isDeleted;
}
