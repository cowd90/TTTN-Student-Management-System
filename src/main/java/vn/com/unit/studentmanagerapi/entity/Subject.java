package vn.com.unit.studentmanagerapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("bus_subject")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subject {

    @Id
    @Column("subject_id")
    Long subjectID;

    @Column("subject_name")
    String subjectName;

    @Column("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @Column("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;

    @Column("number_of_student")
    int numberOfStudent;

    @Column("number_of_availability")
    int numberOfAvailability;

    @Column("status")
    boolean status;

    @Column("number_of_credit")
    int numberOfCredit;

    @Column("course_id")
    CourseID courseID;

    @Column("tuition")
    long tuition;

    @Column("description")
    String description;

    @Column("created_at")
    LocalDateTime createdAt = LocalDateTime.now();
    @Column("update_at")
    LocalDateTime updatedAt = LocalDateTime.now();
    @Column("update_by")
    String updateBy;
    @Column("is_deleted")
    boolean isDeleted = false;
}
