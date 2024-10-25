package vn.com.unit.studentmanagerapi.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationSubjectID implements Serializable{
    @Column("subject_id")
    Long subjectID;

    @Column("student_id")
    Long studentID;
}
