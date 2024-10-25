package vn.com.unit.studentmanagerapi.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bus_student")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Student {
    @Id
    @Column("student_id")
    Long studentID;

    @Column("full_name")
    String fullName;

    @Column("avatar")
    byte[] avatar;

    @Column("email")
    String email;

    @Column("date_of_birth")
    LocalDate dateOfBirth;

    @Column("gender")
    Character gender;

    @Column("date_of_admission")
    LocalDate dateOfAdmission;

    @Column("created_at")
    LocalDateTime createdAt; // default current timestamp
    @Column("update_at")
    LocalDateTime updateAt; // default current timestamp
    @Column("update_by")
    String updateBy;
    @Column("is_deleted")
    boolean isDeleted; // default false
}
