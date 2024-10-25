package vn.com.unit.studentmanagerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;
import vn.com.unit.studentmanagerapi.repository.StudentRepository;
import vn.com.unit.studentmanagerapi.service.StudentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@TestPropertySource("/test.yml")
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    StudentRepository studentRepository;

    private StudentCreateRequest request;
    private StudentUpdateRequest updateRequest;
    private StudentResponse response;

    private LocalDate dob;
    private LocalDate doa;

    @BeforeEach
    void initData() {

        dob = LocalDate.of(1999, 1, 1);
        doa = LocalDate.of(2019, 8, 15);

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
                .updateAt(LocalDateTime.now())
                .updateBy("admin@gmail.com")
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
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateStudent_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        when(studentService.createStudent(any()))
                .thenReturn(response);
        when(studentService.getAuthEmail()).thenReturn("admin@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/students")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("data.fullName")
                        .value("John Smith")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("data.email")
                        .value("john.smith@gmail.com")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("data.gender")
                        .value("MALE")
                )
        ;
    }

    @Test
    void testCreateStudent_fullNameEmpty_fail() throws Exception {
        request.setFullName("");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_FULL_NAME_NULL_EMPTY")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Full name cannot be null or empty")
                )
        ;
    }

    @Test
    void testCreateStudent_fullNameNull_fail() throws Exception {
        request.setFullName(null);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_FULL_NAME_NULL_EMPTY")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Full name cannot be null or empty")
                )
        ;
    }

    @Test
    void testCreateStudent_emailEmpty_fail() throws Exception {
        request.setEmail("");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_NULL_EMPTY")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email cannot be null or empty")
                )
        ;
    }

    @Test
    void testCreateStudent_emailNull_fail() throws Exception {
        request.setEmail(null);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_NULL_EMPTY")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email cannot be null or empty")
                )
        ;
    }

    @Test
    void testCreateStudent_emailInvalid_fail() throws Exception {
        request.setEmail(".1234@gmail.com");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_INVALID_FORMAT")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email format is invalid")
                )
        ;
    }

    @Test
    void testCreateStudent_dobNull_fail() throws Exception {
        request.setDateOfBirth(null);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_DOB_NULL")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Date of birth cannot be null")
                )
        ;
    }

    @Test
    void testCreateStudent_genderNull_fail() throws Exception {
        request.setGender(null);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_GENDER_NULL")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Gender cannot be null")
                )
        ;
    }

    @Test
    void testCreateStudent_doaNull_fail() throws Exception {
        request.setDateOfAdmission(null);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_DOA_NULL")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Date of admission cannot be null")
                )
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(studentService.updateStudent("email@gmail.com", updateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/students/{email}", "email@gmail.com")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
            .andExpect(MockMvcResultMatchers.jsonPath("code")
                    .value("APP_SUCCESS"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_fullNameNull_fail() throws Exception {
        updateRequest.setFullName(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(studentService.updateStudent("email@gmail.com", updateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students/{email}", "email@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_FULL_NAME_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Full name cannot be null or empty"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_fullNameEmpty_fail() throws Exception {
        updateRequest.setFullName("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(studentService.updateStudent("email@gmail.com", updateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students/{email}", "email@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_FULL_NAME_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Full name cannot be null or empty"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_fullNameTooLong_fail() throws Exception {
        String name = new String(new char[101]).replace("\0", "a");
        updateRequest.setFullName(name);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(studentService.updateStudent("email@gmail.com", updateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students/{email}", "email@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_FULL_NAME_TOO_LONG"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Full name cannot exceed 100 characters"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_dobInFuture_fail() throws Exception {
        updateRequest.setDateOfBirth(LocalDate.now().plusDays(1L));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(studentService.updateStudent("email@gmail.com", updateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students/{email}", "email@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_DOB_IN_FUTURE"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Date of birth cannot be in the future"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateStudent_genderInvalid_fail() throws Exception {
        updateRequest.setGender("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        when(studentService.updateStudent("email@gmail.com", updateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students/{email}", "email@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("STU_GENDER_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Gender must be 'Male', 'Female', or 'Other'"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUploadPhoto_validRequest_success() throws Exception {
        // Prepare mock data
        String email = "test@example.com";
        MockMultipartFile avatar = new MockMultipartFile(
                "avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes()
        );

        StudentResponse studentResponse = new StudentResponse();
        // Set necessary fields on studentResponse

        // Mock the service layer
        when(studentService.uploadPhoto(eq(email), any(MultipartFile.class)))
                .thenReturn(studentResponse);

        // Perform the test
        mockMvc.perform(multipart("/students/upload/{email}", email)
                        .file(avatar))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testGetStudentByEmail_validRequest_success() throws Exception {
        when(studentService.findStudentByEmail("email@gmail.com")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/{email}", "email@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("email@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testDeleteStudentById_validRequest_success() throws Exception {
        when(studentService.deleteStudentById(1L)).thenReturn(anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testSearchStudentByName_validRequest_success() throws Exception {
        StudentResponse student1 = new StudentResponse();
        StudentResponse student2 = new StudentResponse();

        List<StudentResponse> studentResponses = Arrays.asList(student1, student2);

        // Mock the service layer
        when(studentService.searchByName("abc", 0, 10))
                .thenReturn(studentResponses);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/search")
                        .param("term", "abc")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testSearchStudentByAdmissionDate_validRequest_success() throws Exception {
        StudentResponse student1 = new StudentResponse();
        StudentResponse student2 = new StudentResponse();

        List<StudentResponse> studentResponses = Arrays.asList(student1, student2);

        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 1, 1);

        // Mock the service layer
        when(studentService.searchByAdmissionDateRange(startDate, endDate, 0, 10))
                .thenReturn(studentResponses);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/search-by-date")
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate))
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"))
        ;
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testGetMyInfo_validRequest_success() throws Exception {
        when(studentService.getMyInfo()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/my-info")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"))
        ;
    }

}
