package vn.com.unit.studentmanagerapi.service;

import org.springframework.web.multipart.MultipartFile;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;

import java.time.LocalDate;
import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentCreateRequest request);
    StudentResponse updateStudent(String email, StudentUpdateRequest request);
    StudentResponse uploadPhoto(String email, MultipartFile photo);
    StudentResponse findStudentByEmail(String email);
    String deleteStudentById(Long studentId);
    List<StudentResponse> searchByName(String condition, int page, int size);
    List<StudentResponse> searchByAdmissionDateRange(LocalDate start, LocalDate end, int page, int size);
    StudentResponse getMyInfo();
    String getAuthEmail();
}
