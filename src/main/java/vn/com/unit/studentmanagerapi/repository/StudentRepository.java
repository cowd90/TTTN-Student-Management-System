package vn.com.unit.studentmanagerapi.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import vn.com.unit.studentmanagerapi.entity.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, Long> {

    @Query("SELECT * FROM bus_student WHERE student_id = :id AND is_deleted = FALSE")
    Optional<Student> findByStudentID(Long id);

    Optional<Student> findByEmail(String email);

    @Modifying
    @Query("UPDATE bus_student SET is_deleted = TRUE WHERE student_id = :studentId")
    boolean softDeleteByStudentID(Long studentId);

    @Query("SELECT * FROM bus_student WHERE LOWER(full_name) LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND is_deleted = FALSE ORDER BY full_name LIMIT :limit OFFSET :offset")
    List<Student> findAllByFullName(String fullName, int limit, int offset);

    @Query("SELECT * FROM bus_student WHERE date_of_admission BETWEEN :startDate AND :endDate " +
            "AND is_deleted = FALSE ORDER BY full_name LIMIT :limit OFFSET :offset")
    List<Student> findAllByAdmissionDate(LocalDate startDate, LocalDate endDate, int limit, int offset);
}
