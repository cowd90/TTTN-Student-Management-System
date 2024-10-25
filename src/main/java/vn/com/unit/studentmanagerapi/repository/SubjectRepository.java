package vn.com.unit.studentmanagerapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends PagingAndSortingRepository<Subject, Long> {
    @Modifying
    @Query("update bus_subject " +
            " set is_deleted = :newValue, update_at = :updateAt, update_by = :updateBy" +
            " where subject_id = :subjectID")
    boolean updateIsDeletedBySubjectID(Long subjectID, LocalDateTime updateAt, String updateBy, boolean newValue);

    Optional<Subject> searchBySubjectIDAndIsDeleted(Long subjectID, boolean isDelete);

    Page<Subject> searchByCourseIDAndIsDeleted(CourseID courseID, boolean isDelete, Pageable pageRequest);

    Page<Subject> searchByCourseID(CourseID courseID, Pageable pageRequest);

    Page<Subject> searchByIsDeleted(boolean isDelete, Pageable pageRequest);

    @Query(" select * from bus_subject " +
            " where subject_id in (select subject_id from bus_registration_subject " +
            "                     where student_id = :studentID) ")
    List<Subject> searchByStudentID(Long studentID);
}
