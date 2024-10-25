package vn.com.unit.studentmanagerapi.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import vn.com.unit.studentmanagerapi.entity.RegistrationSubject;
import vn.com.unit.studentmanagerapi.entity.RegistrationSubjectID;

@Repository
public interface RegistrationSubjectRepository extends PagingAndSortingRepository<RegistrationSubject, RegistrationSubjectID> {
    @Modifying
    @Query(
            "insert into bus_registration_subject (subject_id, student_id, update_by) " +
                    " select :subjectID, :studentID, :updateBy" +
                    " where (select count(rs.subject_id) from bus_registration_subject rs " +
                    "        inner join bus_subject s " +
                    "        on  rs.subject_id = s.subject_id " +
                    "        where s.course_id = (select course_id from bus_subject " +
                    "                                where subject_id = :subjectID " +
                    "                                limit 1) and rs.student_id = :studentID" +
                    " ) < :subjectLimitForCourse " +  // không quá 5 môn 1 học kỳ
                    " and " +
                    " ( select count(subject_id) as number_of_student_register " +
                    " from bus_registration_subject " +
                    " where subject_id = :subjectID ) < (select number_of_student " +
                    " from bus_subject where subject_id = :subjectID )" // còn slot không
    )
    boolean insert(Long subjectID, Long studentID, String updateBy, int subjectLimitForCourse);

    @Modifying
    @Query("delete from bus_registration_subject where subject_id = :subjectID and student_id = :studentID")
    boolean deleteById(Long subjectID, Long studentID);
}
