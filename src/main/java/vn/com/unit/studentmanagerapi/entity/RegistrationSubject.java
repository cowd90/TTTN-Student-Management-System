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

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("bus_registration_subject")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationSubject {

    @Id
    RegistrationSubjectID registrationSubjectID;

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();
    @Column("update_at")
    LocalDateTime updateAt = LocalDateTime.now();
    @Column("update_by")
    String updateBy;

}
