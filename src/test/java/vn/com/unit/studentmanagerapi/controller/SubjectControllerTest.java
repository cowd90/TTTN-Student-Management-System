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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vn.com.unit.studentmanagerapi.config.GlobalValue;
import vn.com.unit.studentmanagerapi.config.JwtTokenProvider;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectFieldSearch;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectSortField;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectSearchRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.service.SubjectService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@WebMvcTest(SubjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SubjectControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    GlobalValue globalValue;
    @MockBean
    SubjectService subjectService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Constants
    static final String BEARER_TOKEN= "Bearer token";
    static final Long SUBJECT_ID = 1L;
    static final String BASE_URL = "/subjects";
    static final String CRUD_URL= BASE_URL + '/' + SUBJECT_ID;
    static final String SEARCH_URL = BASE_URL + "/search";

    // Validation constants
    static final Integer NUMBER_OF_STUDENTS_MAX = 200;
    static final Integer NUMBER_OF_CREDIT_MAX = 10;
    static final Long TUITION_MIN = 1000L;
    static final Long TUITION_MAX = 10000000L;

    @Test
    void testCreateSubject_STT01() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));
        Mockito.when(subjectService.createSubject(subjectCreateRequest)).thenReturn(true);

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT02() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .courseID(CourseID.K02)
                .description("Môn học ngày giúp sinh viên học thêm công nghệ mới")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));
        Mockito.when(subjectService.createSubject(subjectCreateRequest)).thenReturn(true);

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT03() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .subjectName("")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_IS_EMPTY));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT04() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .subjectName("Mới")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_TOO_SHORT));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT05() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .subjectName("Phân tích và thiết kế hệ thống thông tin hiện đại dựa trên các nguyên lý của kiến trúc phần mềm, kỹ thuật lập trình hướng đối tượng, bảo mật hệ thống và ứng dụng công nghệ thông tin tiên tiến trong phát triển phần mềm quản lý và xử lý dữ liệu lớn")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_TOO_LONG));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT06() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .subjectName("Công nghệ @")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_CONTAINING_SPECIAL_CHARACTERS));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT07() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .startDate(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.START_DATE_NULL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT08() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .startDate(LocalDate.parse("1900-01-01")) // dữ liệu tạm để replace
                .build();
        String subjectCreateRequestJson = objectMapper.writeValueAsString(subjectCreateRequest).replace("[1900,1,1]", "\"hi\"");
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.DATE_INVALID));

        performPostRequest(subjectCreateRequestJson)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT10() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .startDate(LocalDate.now())
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.START_DATE_BEFORE_CURRENT_DATE));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT11() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .endDate(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.END_DATE_NULL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT12() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .endDate(LocalDate.parse("1900-01-01")) // dữ liệu tạm để replace
                .build();
        String subjectCreateRequestJson = objectMapper.writeValueAsString(subjectCreateRequest).replace("[1900,1,1]", "\"hi\"");
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.DATE_INVALID));

        performPostRequest(subjectCreateRequestJson)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT14() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .startDate(LocalDate.now().plusDays(31)) // endate +30 day
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.END_DATE_BEFORE_START_DATE));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT15() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .numberOfStudent(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_STUDENT_NULL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT16() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .numberOfStudent(-1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_STUDENT_POSITIVE_INT));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT17() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .numberOfStudent(NUMBER_OF_STUDENTS_MAX+1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_STUDENTS_EXCEEDS_LIMIT));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT18() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .numberOfCredit(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_CREDIT_NULL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT19() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .numberOfCredit(-1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_CREDIT_POSITIVE_INT));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT20() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .numberOfCredit(NUMBER_OF_CREDIT_MAX+1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_CREDIT_EXCEEDS_LIMIT));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT21() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .courseID(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.COURSE_ID_NULL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT22() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .courseID(CourseID.K01) // // dữ liệu tạm để replace
                .build();
        String subjectCreateRequestJson = objectMapper.writeValueAsString(subjectCreateRequest).replace("K01", "K03");
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.COURSE_ID_INVALID));

        performPostRequest(subjectCreateRequestJson)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT23() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .tuition(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.TUITION_NULL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }


    @Test
    void testCreateSubject_STT24() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .tuition(TUITION_MIN-1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.TUITION_TOO_SMALL));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT25() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .tuition(TUITION_MAX+1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.TUITION_TOO_BIG));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    @Test
    void testCreateSubject_STT26() throws Exception {
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder()
                .description("  ")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.DESC_EMPTY));

        performPostRequest(subjectCreateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).createSubject(subjectCreateRequest);
    }

    // ========= updateSubject ========= \\

    @Test
    void updateSubject_STT01() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder().build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));
        Mockito.when(subjectService.updateSubject(SUBJECT_ID, subjectUpdateRequest)).thenReturn(true);

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT02() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .courseID(CourseID.K02)
                .description("Môn học ngày giúp sinh viên học thêm công nghệ mới")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));
        Mockito.when(subjectService.updateSubject(SUBJECT_ID, subjectUpdateRequest)).thenReturn(true);

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT03() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .subjectName("")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_IS_EMPTY));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT04() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .subjectName("Mới")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_TOO_SHORT));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT05() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .subjectName("Phân tích và thiết kế hệ thống thông tin hiện đại dựa trên các nguyên lý của kiến trúc phần mềm, kỹ thuật lập trình hướng đối tượng, bảo mật hệ thống và ứng dụng công nghệ thông tin tiên tiến trong phát triển phần mềm quản lý và xử lý dữ liệu lớn")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_TOO_LONG));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT06() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .subjectName("Công nghệ @")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_NAME_CONTAINING_SPECIAL_CHARACTERS));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT07() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .startDate(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.START_DATE_NULL));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT08() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .startDate(LocalDate.parse("1900-01-01")) // dữ liệu tạm để replace
                .build();
        String subjectCreateRequestJson = objectMapper.writeValueAsString(subjectUpdateRequest).replace("[1900,1,1]", "\"hi\"");
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.DATE_INVALID));

        performPutRequest(subjectCreateRequestJson)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT10() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .startDate(LocalDate.now())
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.START_DATE_BEFORE_CURRENT_DATE));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT11() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .endDate(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.END_DATE_NULL));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT12() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .endDate(LocalDate.parse("1900-01-01")) // dữ liệu tạm để replace
                .build();
        String subjectCreateRequestJson = objectMapper.writeValueAsString(subjectUpdateRequest).replace("[1900,1,1]", "\"hi\"");
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.DATE_INVALID));

        performPutRequest(subjectCreateRequestJson)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT14() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .startDate(LocalDate.now().plusDays(31)) // end date +30 day
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.END_DATE_BEFORE_START_DATE));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT18() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .numberOfCredit(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_CREDIT_NULL));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT19() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .numberOfCredit(-1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_CREDIT_POSITIVE_INT));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT20() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .numberOfCredit(NUMBER_OF_CREDIT_MAX + 1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.NUMBER_OF_CREDIT_EXCEEDS_LIMIT));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT21() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .courseID(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.COURSE_ID_NULL));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT22() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .courseID(CourseID.K01) // // dữ liệu tạm để replace
                .build();
        String subjectCreateRequestJson = objectMapper.writeValueAsString(subjectUpdateRequest).replace("K01", "K03");
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.COURSE_ID_INVALID));

        performPutRequest(subjectCreateRequestJson)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT23() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .tuition(null)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.TUITION_NULL));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }


    @Test
    void updateSubject_STT24() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .tuition(TUITION_MIN - 1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.TUITION_TOO_SMALL));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT25() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .tuition(TUITION_MAX + 1)
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.TUITION_TOO_BIG));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void updateSubject_STT26() throws Exception {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder()
                .description("  ")
                .build();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.DESC_EMPTY));

        performPutRequest(subjectUpdateRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService, Mockito.never()).updateSubject(SUBJECT_ID, subjectUpdateRequest);
    }

    @Test
    void testDeleteSubject_STT01() throws Exception {
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(true));
        Mockito.when(subjectService.deleteSubject(SUBJECT_ID)).thenReturn(true);

        performDeleteRequest()
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).deleteSubject(SUBJECT_ID);
    }

    @Test
    void testDeleteSubject_STT02() throws Exception {
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(false));
        Mockito.when(subjectService.deleteSubject(SUBJECT_ID)).thenReturn(false);

        performDeleteRequest()
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).deleteSubject(SUBJECT_ID);
    }

    @Test
    void testGetBySubjectID_STT01() throws Exception {
        Subject subject = getSubject();
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(subject));
        Mockito.when(subjectService.getBySubjectID(SUBJECT_ID)).thenReturn(subject);

        performGetRequest(CRUD_URL)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).getBySubjectID(SUBJECT_ID);
    }

    @Test
    void testGetBySubjectID_STT02() throws Exception {
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(ErrorCode.SUBJECT_ID_INVALID));
        Mockito.when(subjectService.getBySubjectID(SUBJECT_ID))
                        .thenThrow(new AppException(ErrorCode.SUBJECT_ID_INVALID));

        performGetRequest(CRUD_URL)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).getBySubjectID(SUBJECT_ID);
    }


    @Test
    void testSearch() throws Exception {
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest();
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(getSubject());
        Page<Subject> subject = new PageImpl<>(subjects);
        String responseExpect = objectMapper.writeValueAsString(getApiResponse(subject));
        Mockito.when(subjectService.search(subjectSearchRequest)).thenReturn(subject);

        performGetRequest(SEARCH_URL)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(responseExpect));
        Mockito.verify(subjectService).search(subjectSearchRequest);
    }


    private ResultActions performGetRequest(String url) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private ResultActions performPostRequest(Object content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private ResultActions performPostRequest(String content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private ResultActions performPutRequest(Object content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.put(CRUD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content))
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private ResultActions performPutRequest(String content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.put(CRUD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.delete(CRUD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN));
    }

    private <T> ApiResponse<T> getApiResponse(T data) {
        return ApiResponse.<T>builder()
                .code("APP_SUCCESS")
                .message("Success")
                .data(data)
                .build();
    }

    private ApiResponse getApiResponse(ErrorCode errorCode) {
        return ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }


    private SubjectCreateRequest.SubjectCreateRequestBuilder getSubjectCreateRequestBuilder() {
        return SubjectCreateRequest.builder()
                .subjectName("Công nghệ mới")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .numberOfStudent(30)
                .numberOfCredit(3)
                .courseID(CourseID.K01)
                .tuition(400000L)
                .description(null);
    }

    private SubjectUpdateRequest.SubjectUpdateRequestBuilder getSubjectUpdateRequestBuilder() {
        return SubjectUpdateRequest.builder()
                .subjectName("Công nghệ mới")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .numberOfCredit(3)
                .courseID(CourseID.K01)
                .tuition(400000L)
                .description(null);
    }

    private Subject getSubject(){
        return Subject.builder()
                        .subjectID(SUBJECT_ID)
                        .subjectName("Công nghệ mới")
                        .startDate(LocalDate.now().plusDays(1))
                        .endDate(LocalDate.now().plusDays(30))
                        .numberOfStudent(30)
                        .numberOfCredit(3)
                        .courseID(CourseID.K01)
                        .tuition(400000L)
                        .description(null)
                        .isDeleted(false)
                        .build();
    }

    private SubjectSearchRequest getSubjectSearchRequest(){
        return SubjectSearchRequest.builder()
                .fieldSearch(SubjectFieldSearch.DEFAULT)
                .valueSearch("")
                .page(globalValue.getPageDefault())
                .size(globalValue.getSizeDefault())
                .sortField(SubjectSortField.subjectID)
                .sortDirection(Sort.Direction.ASC)
                .build();
    }
}
