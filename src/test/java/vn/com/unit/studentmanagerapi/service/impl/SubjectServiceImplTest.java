package vn.com.unit.studentmanagerapi.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectFieldSearch;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectSortField;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectSearchRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectUpdateRequest;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.repository.SubjectRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SubjectServiceImplTest {
    @InjectMocks
    SubjectServiceImpl subjectService;
    @Mock
    SubjectRepository subjectRepository;

    SecurityContext  securityContext;
    Authentication authentication;

    static final Long SUBJECT_ID = 1L;
    static final CourseID COURSE_ID = CourseID.K01;
    static final Long STUDENT_ID = 1L;

    static final boolean IS_DELETED = true;
    static final boolean IS_ADMIN = true;

    static final int PAGING_PAGE = 0;
    static final int PAGING_SIZE = 10;

    static final SubjectSortField PAGING_FIELD_SORT = SubjectSortField.subjectID;

    void setUp(Boolean isAdmin){
        UserDetails userDetails = Account.builder()
                .email("sonnees@mail.com")
                .password("12345678")
                .role( isAdmin == null ? 'N' : (isAdmin ? 'A' : 'T'))
                .build();

        securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateSubject_STT01() {
        setUp(IS_ADMIN);
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder().build();

        Mockito.when(subjectRepository.save(Mockito.any(Subject.class))).thenReturn(null);

        Boolean resultActual = subjectService.createSubject(subjectCreateRequest);

        Assertions.assertTrue(resultActual);
        Mockito.verify(subjectRepository).save(Mockito.any(Subject.class));
        checkSecurityContextUse();
    }

    @Test
    void testCreateSubject_STT02() {
        setUp(IS_ADMIN);
        SubjectCreateRequest subjectCreateRequest = getSubjectCreateRequestBuilder().build();

        Mockito.when(subjectRepository.save(Mockito.any(Subject.class))).thenThrow(new DuplicateKeyException("Error"));

        DuplicateKeyException resultActual = Assertions.assertThrows(DuplicateKeyException.class, () -> subjectService.createSubject(subjectCreateRequest));

        Assertions.assertEquals(DuplicateKeyException.class, resultActual.getClass());
        Mockito.verify(subjectRepository).save(Mockito.any(Subject.class));
        checkSecurityContextUse();
    }


    @Test
    void testUpdateSubject_STT01() {
        setUp(IS_ADMIN);
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder().build();

        Mockito.when(subjectRepository.findById(SUBJECT_ID)).thenReturn(getOptionalSubject());
        Mockito.when(subjectRepository.save(Mockito.any(Subject.class))).thenReturn(null);

        Boolean resultActual = subjectService.updateSubject(SUBJECT_ID, subjectUpdateRequest);

        Assertions.assertTrue(resultActual);
        Mockito.verify(subjectRepository).findById(SUBJECT_ID);
        Mockito.verify(subjectRepository).save(Mockito.any(Subject.class));
        checkSecurityContextUse();
    }

    @Test
    void testUpdateSubject_STT02() {
        SubjectUpdateRequest subjectUpdateRequest = getSubjectUpdateRequestBuilder().build();

        Mockito.when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> subjectService.updateSubject(SUBJECT_ID, subjectUpdateRequest));

        Assertions.assertEquals(AppException.class, resultActual.getClass());
        Mockito.verify(subjectRepository).findById(SUBJECT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).save(Mockito.any(Subject.class));
    }

    @Test
    void testDeleteSubject_STT01() {
        setUp(IS_ADMIN);
        Mockito.when(subjectRepository
                .updateIsDeletedBySubjectID(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);

        Boolean resultActual = subjectService.deleteSubject(SUBJECT_ID);

        Assertions.assertTrue(resultActual);
        Mockito.verify(subjectRepository)
                .updateIsDeletedBySubjectID(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.anyString(),Mockito.anyBoolean());
        checkSecurityContextUse();
    }

    @Test
    void testDeleteSubject_STT02() {
        setUp(IS_ADMIN);
        Mockito.when(
                subjectRepository.updateIsDeletedBySubjectID(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.anyString(), Mockito.anyBoolean())
        ).thenReturn(false);

        Boolean resultActual = subjectService.deleteSubject(SUBJECT_ID);

        Assertions.assertFalse(resultActual);
        Mockito.verify(subjectRepository)
                .updateIsDeletedBySubjectID(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.anyString(),Mockito.anyBoolean());
        checkSecurityContextUse();
    }

    @Test
    void testGetBySubjectID_STT01() {
        setUp(IS_ADMIN);
        Subject subject = getSubject();

        Mockito.when(subjectRepository.findById(SUBJECT_ID)).thenReturn(getOptionalSubject());

        Subject resultActual = subjectService.getBySubjectID(SUBJECT_ID);

        Assertions.assertEquals(subject, resultActual);

        Mockito.verify(subjectRepository).findById(SUBJECT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        checkSecurityContextUse();
    }

    @Test
    void testGetBySubjectID_STT02() {
        setUp(!IS_ADMIN);
        Subject subject = getSubject();
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(getOptionalSubject());

        Subject resultActual = subjectService.getBySubjectID(SUBJECT_ID);

        Assertions.assertEquals(subject, resultActual);

        Mockito.verify(subjectRepository, Mockito.never()).findById(SUBJECT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        checkSecurityContextUse();
    }

    @Test
    void testGetBySubjectID_STT03() {
        setUp(!IS_ADMIN);
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> subjectService.getBySubjectID(SUBJECT_ID));

        Assertions.assertEquals(ErrorCode.SUBJECT_ID_INVALID, resultActual.getErrorCode());

        Mockito.verify(subjectRepository, Mockito.never()).findById(SUBJECT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        checkSecurityContextUse();
    }

    @Test
    void testGetBySubjectID_Unauthorized() {
        setUp(null);

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> subjectService.getBySubjectID(SUBJECT_ID));
        Assertions.assertEquals(ErrorCode.UNAUTHORIZED, resultActual.getErrorCode());

        Mockito.verify(subjectRepository, Mockito.never()).findById(SUBJECT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        checkSecurityContextUse();
    }

    @Test
    void testSearch_STT01_FindAll_ADMIN() {
        setUp(IS_ADMIN);
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest().build();
        Page<Subject> resultExpected = getSubjectPage(subjectSearchRequest);

        PageRequest pageable = getPageRequest(subjectSearchRequest);
        Mockito.when(subjectRepository.findAll(pageable)).thenReturn(resultExpected);

        Page<Subject> resultActual = subjectService.search(subjectSearchRequest);

        Assertions.assertEquals(resultExpected, resultActual);

        Mockito.verify(subjectRepository).findAll(pageable);
        checkSecurityContextUse();
    }

    @Test
    void testSearch_STT02_FindAll_Student() {
        setUp(!IS_ADMIN);
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest().build();
        Page<Subject> resultExpected = getSubjectPage(subjectSearchRequest);

        PageRequest pageable = getPageRequest(subjectSearchRequest);
        Mockito.when(subjectRepository.searchByIsDeleted(!IS_DELETED, pageable)).thenReturn(resultExpected);

        Page<Subject> resultActual = subjectService.search(subjectSearchRequest);

        Assertions.assertEquals(resultExpected, resultActual);

        Mockito.verify(subjectRepository).searchByIsDeleted(!IS_DELETED, pageable);
        checkSecurityContextUse();
    }

    @Test
    void testSearch_STT03_ByCourseID_Admin() {
        setUp(IS_ADMIN);
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest().fieldSearch(SubjectFieldSearch.COURSE_ID).valueSearch(String.valueOf(COURSE_ID)).build();
        Page<Subject> resultExpected = getSubjectPage(subjectSearchRequest);

        PageRequest pageable = getPageRequest(subjectSearchRequest);
        Mockito.when(subjectRepository.searchByCourseID(COURSE_ID,pageable)).thenReturn(resultExpected);

        Page<Subject> resultActual = subjectService.search(subjectSearchRequest);

        Assertions.assertEquals(resultExpected, resultActual);

        Mockito.verify(subjectRepository).searchByCourseID(COURSE_ID, pageable);
        checkSecurityContextUse();
    }

    @Test
    void testSearch_STT04_ByCourseID_Student() {
        setUp(!IS_ADMIN);
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest()
                .fieldSearch(SubjectFieldSearch.COURSE_ID)
                .valueSearch(COURSE_ID.toString())
                .build();
        Page<Subject> resultExpected = getSubjectPage(subjectSearchRequest);

        PageRequest pageable = getPageRequest(subjectSearchRequest);
        Mockito.when(subjectRepository.searchByCourseIDAndIsDeleted(COURSE_ID, !IS_DELETED,pageable)).thenReturn(resultExpected);

        Page<Subject> resultActual = subjectService.search(subjectSearchRequest);

        Assertions.assertEquals(resultExpected, resultActual);

        Mockito.verify(subjectRepository).searchByCourseIDAndIsDeleted(COURSE_ID, !IS_DELETED,pageable);
        checkSecurityContextUse();
    }

    @Test
    void testSearch_STT05_ByStudentID() {
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest()
                .fieldSearch(SubjectFieldSearch.STUDENT_ID)
                .valueSearch(STUDENT_ID.toString())
                .build();
        Page<Subject> resultExpected = getSubjectPage(subjectSearchRequest);
        List<Subject> subjectList = getSubjectList();

        Mockito.when(subjectRepository.searchByStudentID(STUDENT_ID)).thenReturn(subjectList);

        Page<Subject> resultActual = subjectService.search(subjectSearchRequest);

        Assertions.assertEquals(resultExpected.getSize(), resultActual.getSize());
        Mockito.verify(subjectRepository).searchByStudentID(STUDENT_ID);
    }

    @Test
    void testSearch_STT06_ByStudentID() {
        SubjectSearchRequest subjectSearchRequest = getSubjectSearchRequest()
                .fieldSearch(SubjectFieldSearch.STUDENT_ID)
                .valueSearch(STUDENT_ID.toString())
                .sortDirection(Sort.Direction.DESC)
                .build();
        Page<Subject> resultExpected = getSubjectPage(subjectSearchRequest);
        List<Subject> subjectList = getSubjectList();

        Mockito.when(subjectRepository.searchByStudentID(STUDENT_ID)).thenReturn(subjectList);

        Page<Subject> resultActual = subjectService.search(subjectSearchRequest);

        Assertions.assertEquals(resultExpected.getSize(), resultActual.getSize());
        Mockito.verify(subjectRepository).searchByStudentID(STUDENT_ID);
    }


    private List<Subject> getSubjectList() {
        ArrayList<Subject> subjectArrayList = new ArrayList<>();
        subjectArrayList.add(getSubject());
        return subjectArrayList;
    }

    private Page<Subject> getSubjectPage(SubjectSearchRequest subjectSearchRequest) {
        return new PageImpl<>(getSubjectList(), getPageRequest(subjectSearchRequest), getSubjectList().size());
    }

    private static PageRequest getPageRequest(SubjectSearchRequest subjectSearchRequest) {
        return PageRequest.of(
                subjectSearchRequest.getPage(),
                subjectSearchRequest.getSize(),
                Sort.by(
                        subjectSearchRequest.getSortDirection() == Sort.Direction.ASC
                                ? Sort.Order.asc(subjectSearchRequest.getSortField() + "")
                                : Sort.Order.desc(subjectSearchRequest.getSortField() + "")
                )
        );
    }

    private void checkSecurityContextUse(){
        Mockito.verify(securityContext, Mockito.atMost(2)).getAuthentication();
        Mockito.verify(authentication, Mockito.atMost(2)).getPrincipal();
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
                .status(true)
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
                        .courseID(COURSE_ID)
                        .tuition(400000L)
                        .description(null)
                        .isDeleted(!IS_DELETED)
                        .build();
    }

    private Optional<Subject> getOptionalSubject(){
        return Optional.of(getSubject());
    }

    private SubjectSearchRequest.SubjectSearchRequestBuilder getSubjectSearchRequest(){
        return SubjectSearchRequest.builder()
                .fieldSearch(SubjectFieldSearch.DEFAULT)
                .valueSearch("")
                .page(PAGING_PAGE)
                .size(PAGING_SIZE)
                .sortField(PAGING_FIELD_SORT)
                .sortDirection(Sort.Direction.ASC);
    }
}
