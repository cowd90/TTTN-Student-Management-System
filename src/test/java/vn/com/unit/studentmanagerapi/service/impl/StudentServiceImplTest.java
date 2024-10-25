package vn.com.unit.studentmanagerapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.Student;
import vn.com.unit.studentmanagerapi.entity.enums.Gender;
import vn.com.unit.studentmanagerapi.entity.enums.Role;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.mapper.StudentMapper;
import vn.com.unit.studentmanagerapi.repository.AccountRepository;
import vn.com.unit.studentmanagerapi.repository.StudentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@TestPropertySource("/test.yml")
class StudentServiceImplTest {
    @Autowired
    private StudentServiceImpl studentService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private AccountServiceImpl accountService;

    @Mock
    private StudentMapper studentMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    private MockMultipartFile photo;

    private Student student;

    private StudentCreateRequest request;
    private StudentUpdateRequest updateRequest;
    private StudentResponse response;

    @BeforeEach
    void initData() {

        LocalDate dob = LocalDate.of(1999, 1, 1);
        LocalDate doa = LocalDate.of(2019, 8, 15);

        request = StudentCreateRequest.builder()
                .fullName("John Smith")
                .email("john.smith@gmail.com")
                .password("12345678")
                .dateOfBirth(dob)
                .dateOfAdmission(doa)
                .gender("MALE")
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .updateBy("admin@gmail.com")
                .build();

        updateRequest = StudentUpdateRequest.builder()
                .fullName("John Smith")
                .dateOfBirth(dob)
                .gender("MALE")
                .build();

        response = StudentResponse.builder()
                .studentID(1L)
                .fullName("John Smith")
                .email("john.smith@gmail.com")
                .gender("MALE")
                .dateOfBirth(dob)
                .dateOfAdmission(doa)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .updateBy("admin@gmail.com")
                .isDeleted(false)
                .build();

        student = Student.builder()
                .fullName("John Smith")
                .email("john.smith@gmail.com")
                .gender(Gender.MALE.getCode())
                .dateOfBirth(dob)
                .dateOfAdmission(doa)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .updateBy("admin@gmail.com")
                .isDeleted(false)
                .build();

        photo = new MockMultipartFile("photo", "photo.jpg",
                "image/jpeg", "dummy image content".getBytes());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void testCreateStudent_validRequest_success() {

        Account account = Account.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.STUDENT.getCode())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .updateBy("john.smith@gmail.com")
                .isDeleted(false)
                .build();

        when(accountRepository.existsByEmail(account.getEmail())).thenReturn(false);
        when(accountService.createAccount(AccountCreateRequest.builder()
                .email(account.getEmail())
                .password(account.getPassword())
                .build())).thenReturn(new AccountResponse());
        when(studentRepository.save(any())).thenReturn(student);

        response = studentService.createStudent(request);

        assertThat(response.getFullName()).isEqualTo("John Smith");
        assertThat(response.getEmail()).isEqualTo("john.smith@gmail.com");
        assertThat(response.getGender()).isEqualTo("MALE");
        assertThat(response.getDateOfBirth()).isEqualTo("1999-01-01");
        assertThat(response.getDateOfAdmission()).isEqualTo("2019-08-15");
        assertThat(response.getUpdateBy()).isEqualTo("admin@gmail.com");
        assertThat(response.isDeleted()).isFalse();

    }

    @Test
    void testCreateStudent_studentExisted_fail() {
        when(accountRepository.existsByEmail(anyString())).thenReturn(true);

        var exception = assertThrows(AppException.class,
                () -> studentService.createStudent(request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo("ACC_EXIST");
        assertThat(exception.getErrorCode().getMessage()).isEqualTo("Email exists");
    }

    @Test
    void testCreateStudent_dobInFuture_fail() {
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);

        request.setDateOfBirth(LocalDate.now().plus(10, ChronoUnit.DAYS));

        var exception = assertThrows(AppException.class,
                () -> studentService.createStudent(request));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_DOB_IN_FUTURE");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Date of birth cannot be in the future");
    }

    @Test
    void testCreateStudent_dobIsAfterDateOfAdmission_fail() {
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);

        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setDateOfAdmission(request.getDateOfBirth().minus(10, ChronoUnit.DAYS));

        var exception = assertThrows(AppException.class,
                () -> studentService.createStudent(request));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_DOA_BEFORE_DOB");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Date of admission cannot be before date of birth");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_validRequest_success() {
        when(studentRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(student));
        when(studentRepository.save(any())).thenReturn(student);

        response = studentService.updateStudent("student@gmail.com", updateRequest);

        assertThat(response.getFullName()).isEqualTo("John Smith");
        assertThat(response.getGender()).isEqualTo("MALE");
        assertThat(response.getDateOfBirth()).isEqualTo("1999-01-01");
        assertThat(response.getUpdateBy()).isEqualTo("admin@gmail.com");
        assertThat(response.isDeleted()).isFalse();
    }

    @Test
    void testUpdateStudent_studentNotExist_fail() {
        when(studentRepository.findById(any())).thenReturn(Optional.empty());
        when(studentRepository.save(any())).thenReturn(student);


        var exception = assertThrows(AppException.class,
                () -> studentService.updateStudent("student@gmail.com", updateRequest));

        assertThat(exception.getErrorCode().getCode()).isEqualTo("STU_NOT_EXIST");
        assertThat(exception.getErrorCode().getMessage()).isEqualTo("Student does not exist");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void testDeleteStudent_validRequest_success() {
        when(studentRepository.existsById(anyLong())).thenReturn(true);
        when(studentRepository.softDeleteByStudentID(anyLong())).thenReturn(true);

        String message = studentService.deleteStudentById(1L);

        assertThat(message).isEqualTo("Student has been deleted!");
    }

    @Test
    void testDeleteStudent_uncategorizedError_fail() {
        when(studentRepository.existsById(anyLong())).thenReturn(true);
        when(studentRepository.softDeleteByStudentID(any())).thenReturn(false);

        var exception = assertThrows(AppException.class,
                () -> studentService.deleteStudentById(1L));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("UNCATEGORIZED_EXCEPTION");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Uncategorized error. Please try again");
    }

    @Test
    void testDeleteStudent_studentNotExist_fail() {
        when(studentRepository.existsById(anyLong())).thenReturn(false);

        var exception = assertThrows(AppException.class,
                () -> studentService.deleteStudentById(1L));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_NOT_EXIST");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Student does not exist");
    }

    @Test
    @WithMockUser(username = "test@student.com")
    void testUploadPhoto_invalidRequest_success() {
        // Mock repository behavior
        when(studentRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(student));

        response = studentService.uploadPhoto("email@gmail.com", photo);

        assertThat(response.getFullName()).isEqualTo("John Smith");
    }

    @Test
    void testUploadPhoto_photoNullOrEmpty_fail() {
        when(studentRepository.findById(any())).thenReturn(Optional.ofNullable(student));
        when(studentRepository.save(any())).thenReturn(student);

        var exception = assertThrows(AppException.class,
                () -> studentService.uploadPhoto("email@gmail.com", null));

        assertThat(exception.getErrorCode().getCode()).isEqualTo("STU_AVATAR_NULL");
        assertThat(exception.getErrorCode().getMessage()).isEqualTo("Avatar cannot be null");
    }

    @Test
    @WithMockUser(username = "test@student.com")
    void testUploadPhoto_studentNotExist_fail() {
        // Mock repository behavior
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                () -> studentService.uploadPhoto("email@gmail.com", photo));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_NOT_EXIST");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Student does not exist");
    }

    @Test
    void testFindStudentByEmail_invalidRequest_success() {
        when(studentRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(student));

        response = studentService.findStudentByEmail("student@gmail.com");

        assertThat(response.getFullName()).isEqualTo("John Smith");

    }

    @Test
    void testFindStudentByEmail_notExist_fail() {
        when(studentRepository.findByEmail("student@gmail.com")).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                () -> studentService.findStudentByEmail("student@gmail.com"));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_NOT_EXIST");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Student does not exist");

    }

    @Test
    void testDeleteStudentById_validRequest_success() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(studentRepository.softDeleteByStudentID(1L)).thenReturn(true);

        String message = studentService.deleteStudentById(1L);

        assertThat(message).isEqualTo("Student has been deleted!");
    }

    @Test
    void testDeleteStudentById_notExist_fail() {
        when(studentRepository.existsById(1L)).thenReturn(false);

        var exception = assertThrows(AppException.class,
                () -> studentService.deleteStudentById(1L));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_NOT_EXIST");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Student does not exist");
    }

    @Test
    void testDeleteStudentById_uncategorizedException_fail() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(studentRepository.softDeleteByStudentID(1L)).thenReturn(false);

        var exception = assertThrows(AppException.class,
                () -> studentService.deleteStudentById(1L));

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("UNCATEGORIZED_EXCEPTION");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Uncategorized error. Please try again");
    }

    @Test
    void testSearchByName_validRequest_success() {
        // Given
        String condition = "John";
        int page = 1;
        int size = 2;
        int offset = page * size;

        Student student1 = new Student();
        Student student2 = new Student();
        List<Student> students = Arrays.asList(student1, student2);

        StudentResponse response1 = new StudentResponse();
        StudentResponse response2 = new StudentResponse();
        List<StudentResponse> expectedResponses = Arrays.asList(response1, response2);

        // When
        when(studentRepository.findAllByFullName(condition, size, offset)).thenReturn(students);
        when(studentMapper.toStudentResponse(student1)).thenReturn(response1);
        when(studentMapper.toStudentResponse(student2)).thenReturn(response2);

        // Then
        List<StudentResponse> actualResponses = studentService.searchByName(condition, page, size);
        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void testSearchByName_pageAndSizeInvalid_fail() {
        // Given
        int page = -1;
        int size = 0;

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByName("John", page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_SEARCH_PAGE_SIZE_INVALID");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Page and size must be positive");

    }

    @Test
    void testSearchByName_pageInvalid_fail() {
        // Given
        int page = -1;
        int size = 10;

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByName("John", page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_SEARCH_PAGE_SIZE_INVALID");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Page and size must be positive");

    }

    @Test
    void testSearchByName_sizeInvalid_fail() {
        // Given
        int page = 0;
        int size = 0;

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByName("John", page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_SEARCH_PAGE_SIZE_INVALID");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Page and size must be positive");

    }

    @Test
    void testSearchByAdmissionDateRange_validRequest_success() {
        // Given
        int page = 1;
        int size = 2;
        int offset = page * size;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 1, 1);

        Student student1 = new Student();
        Student student2 = new Student();
        List<Student> students = Arrays.asList(student1, student2);

        StudentResponse response1 = new StudentResponse();
        StudentResponse response2 = new StudentResponse();
        List<StudentResponse> expectedResponses = Arrays.asList(response1, response2);

        // When
        when(studentRepository.findAllByAdmissionDate(startDate, endDate, size, offset)).thenReturn(students);
        when(studentMapper.toStudentResponse(student1)).thenReturn(response1);
        when(studentMapper.toStudentResponse(student2)).thenReturn(response2);

        // Then
        List<StudentResponse> actualResponses =
                studentService.searchByAdmissionDateRange(startDate, endDate, page, size);
        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void testSearchByAdmissionDateRange_pageAndSizeInvalid_fail() {
        // Given
        int page = -1;
        int size = 0;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 1, 1);

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByAdmissionDateRange(startDate, endDate, page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_SEARCH_PAGE_SIZE_INVALID");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Page and size must be positive");

    }

    @Test
    void testSearchByAdmissionDateRange_pageInvalid_fail() {
        // Given
        int page = -1;
        int size = 10;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 1, 1);

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByAdmissionDateRange(startDate, endDate, page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_SEARCH_PAGE_SIZE_INVALID");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Page and size must be positive");

    }

    @Test
    void testSearchByAdmissionDateRange_sizeInvalid_fail() {
        // Given
        int page = 0;
        int size = 0;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 1, 1);

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByAdmissionDateRange(startDate, endDate, page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_SEARCH_PAGE_SIZE_INVALID");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Page and size must be positive");

    }

    @Test
    void testSearchByAdmissionDateRange_endDateBeforeStartDate_fail() {
        // Given
        int page = 1;
        int size = 10;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2010, 1, 1);

        // When
        var exception = assertThrows(AppException.class,
                () -> studentService.searchByAdmissionDateRange(startDate, endDate, page, size));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("END_DATE_BEFORE_START_DATE");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("End date must be after the start date");

    }

    @Test
    @WithMockUser(username = "student@gmail.com")
    void testGetMyInfo_validRequest_success() {
        when(studentRepository.findByEmail("student@gmail.com"))
                .thenReturn(Optional.ofNullable(student));

        response = studentService.getMyInfo();

        assertThat(response.getFullName()).isEqualTo("John Smith");
        assertThat(response.getEmail()).isEqualTo("john.smith@gmail.com");
        assertThat(response.getGender()).isEqualTo("MALE");
        assertThat(response.getDateOfBirth()).isEqualTo("1999-01-01");
        assertThat(response.getDateOfAdmission()).isEqualTo("2019-08-15");
        assertThat(response.getUpdateBy()).isEqualTo("admin@gmail.com");
        assertThat(response.isDeleted()).isFalse();
    }

    @Test
    @WithMockUser(username = "student@gmail.com")
    void testGetMyInfo_notExist_fail() {
        when(studentRepository.findByEmail("student@gmail.com"))
                .thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                () -> studentService.getMyInfo());

        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("STU_NOT_EXIST");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Student does not exist");
    }

}
