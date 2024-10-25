package vn.com.unit.studentmanagerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vn.com.unit.studentmanagerapi.config.GlobalValue;
import vn.com.unit.studentmanagerapi.config.JwtTokenProvider;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectDeleteRequest;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.service.SubjectRegistrationService;

@WebMvcTest(SubjectRegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestPropertySource(locations = "classpath:test.yaml")
class SubjectRegistrationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    GlobalValue globalValue;
    @MockBean
    SubjectRegistrationService subjectRegistrationService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Constants
    static final String BEARER_TOKEN= "Bearer token";
    static final String BASE_URL= "/subjects/register";

    // Validation constants
    static final Long SUBJECT_ID = 1L;
    static final Long STUDENT_ID = 1L;

    @Test
    void createRegistrationCourse_STT01() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));

        Mockito.when(subjectRegistrationService.createRegistrationSubject(registrationSubjectCreateRequest)).thenReturn(true);

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT02() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().subjectID(null).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_NULL));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT03() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().subjectID(SUBJECT_ID -1).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_NOT_POSITIVE));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT04() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_INVALID));
        Mockito.when(subjectRegistrationService.createRegistrationSubject(registrationSubjectCreateRequest))
                .thenThrow(new AppException(ErrorCode.SUBJECT_ID_INVALID));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT05() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().studentID(null).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.STU_ID_NULL));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT06() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().studentID(SUBJECT_ID -1).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.STU_ID_NOT_POSITIVE));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT07() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.STU_ID_INVALID));
        Mockito.when(subjectRegistrationService.createRegistrationSubject(registrationSubjectCreateRequest))
                .thenThrow(new AppException(ErrorCode.STU_ID_INVALID));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT08() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.COURSE_CLOSED_FOR_REGISTRATION));
        Mockito.when(subjectRegistrationService.createRegistrationSubject(registrationSubjectCreateRequest))
                .thenThrow(new AppException(ErrorCode.COURSE_CLOSED_FOR_REGISTRATION));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void createRegistrationCourse_STT09() throws Exception {
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.EXCEEDS_MAXIMUM_SUBJECTS));
        Mockito.when(subjectRegistrationService.createRegistrationSubject(registrationSubjectCreateRequest))
                .thenThrow(new AppException(ErrorCode.EXCEEDS_MAXIMUM_SUBJECTS));

        performPostRequest(registrationSubjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).createRegistrationSubject(registrationSubjectCreateRequest);
    }

    @Test
    void deleteRegistrationSubject_STT01() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));
        Mockito.when(subjectRegistrationService.deleteRegistrationSubject(registrationSubjectDeleteRequest)).thenReturn(true);

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    @Test
    void deleteRegistrationSubject_STT02() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().subjectID(null).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_NULL));

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    @Test
    void deleteRegistrationSubject_STT03() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().subjectID(SUBJECT_ID-1).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_NOT_POSITIVE));

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    @Test
    void deleteRegistrationSubject_STT04() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_INVALID));
        Mockito.when(subjectRegistrationService.deleteRegistrationSubject(registrationSubjectDeleteRequest))
                .thenThrow(new AppException(ErrorCode.SUBJECT_ID_INVALID));

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    @Test
    void deleteRegistrationSubject_STT05() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().studentID(null).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.STU_ID_NULL));

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    @Test
    void deleteRegistrationSubject_STT06() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().studentID(STUDENT_ID-1).build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.STU_ID_NOT_POSITIVE));

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService, Mockito.never()).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    @Test
    void deleteRegistrationSubject_STT07() throws Exception {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.STU_ID_INVALID));
        Mockito.when(subjectRegistrationService.deleteRegistrationSubject(registrationSubjectDeleteRequest))
                .thenThrow(new AppException(ErrorCode.STU_ID_INVALID));

        performDeleteRequest(registrationSubjectDeleteRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectRegistrationService).deleteRegistrationSubject(registrationSubjectDeleteRequest);
    }

    private ResultActions performPostRequest(Object content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content))
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private ResultActions performDeleteRequest(Object content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content))
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private RegistrationSubjectDeleteRequest.RegistrationSubjectDeleteRequestBuilder getRegistrationSubjectDeleteRequest() {
        return RegistrationSubjectDeleteRequest.builder()
                .subjectID(STUDENT_ID)
                .studentID(STUDENT_ID);
    }


    private RegistrationSubjectCreateRequest.RegistrationSubjectCreateRequestBuilder getRegistrationSubjectCreateRequest() {
        return RegistrationSubjectCreateRequest.builder()
                .studentID(STUDENT_ID)
                .subjectID(SUBJECT_ID);
    }

    private <T> ApiResponse<T> getApiResponse(T data) {
        return ApiResponse.<T>builder()
                .code("APP_SUCCESS")
                .message("Success")
                .data(data)
                .build();
    }

    private ApiResponse getApiResponse(ErrorCode data) {
        return ApiResponse.<ErrorCode>builder()
                .code(data.getCode())
                .message(data.getMessage())
                .data(null)
                .build();
    }

}
