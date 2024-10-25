package vn.com.unit.studentmanagerapi.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import vn.com.unit.studentmanagerapi.config.GlobalValue;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectDeleteRequest;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.RegistrationSubject;
import vn.com.unit.studentmanagerapi.entity.RegistrationSubjectID;
import vn.com.unit.studentmanagerapi.entity.Student;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.repository.RegistrationSubjectRepository;
import vn.com.unit.studentmanagerapi.repository.StudentRepository;
import vn.com.unit.studentmanagerapi.repository.SubjectRepository;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SubjectRegistrationServiceImplTest {

    @InjectMocks
    SubjectRegistrationServiceImpl registrationCourseService;

    @Mock
    RegistrationSubjectRepository registrationSubjectRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    SubjectRepository subjectRepository;

    @Mock
    GlobalValue globalValue;

    SecurityContext securityContext;
    Authentication authentication;

    static final Long SUBJECT_ID = 1L;
    static final CourseID COURSE_ID = CourseID.K01;
    static final Long STUDENT_ID = 1L;
    static final boolean IS_DELETED = true;
    static final boolean IS_STATUS = true;
    static final boolean IS_ADMIN = true;
    static final String GMAIL_OWNER = "owner@mail.com";
    static final String GMAIL_NOT_OWNER = "not_owner@mail.com";
    static final String GMAIL_ADMIN = "admin@mail.com";
    static final int SUBJECT_LIMIT_FOR_COURSE = 5;
    static final String PASSWORD = "12345678";


    void setUp(boolean isAdmin, String email){
        UserDetails userDetails = Account.builder()
                .email(email == null ? GMAIL_ADMIN : email)
                .password(PASSWORD)
                .role(isAdmin ? 'A' : 'T')
                .build();

        securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateRegistrationSubject_STT01_ADMIN() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Optional<Subject> subject = getSubject(IS_STATUS);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(new Student()));
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(subject);
        Mockito.when(registrationSubjectRepository.insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE)).thenReturn(true);
        Mockito.when(globalValue.getSubjectLimitForCourse()).thenReturn(SUBJECT_LIMIT_FOR_COURSE);

        Boolean resultActual = registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest);
        Assertions.assertTrue(resultActual);
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(),registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

    @Test
    void testCreateRegistrationSubject_STT01_STUDENT() {
        setUp(!IS_ADMIN, GMAIL_OWNER);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Optional<Subject> subject = getSubject(IS_STATUS);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_OWNER));
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(subject);
        Mockito.when(registrationSubjectRepository.insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE)).thenReturn(true);
        Mockito.when(globalValue.getSubjectLimitForCourse()).thenReturn(SUBJECT_LIMIT_FOR_COURSE);

        Boolean resultActual = registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest);
        Assertions.assertTrue(resultActual);
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(),registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

    @Test
    void testCreateRegistrationSubject_STT04() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(new Student()));
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest));
        Assertions.assertEquals(ErrorCode.SUBJECT_ID_INVALID, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).insert( registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

    @Test
    void testCreateRegistrationSubject_STT07() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest));
        Assertions.assertEquals(ErrorCode.STU_ID_INVALID, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

    @Test
    void testCreateRegistrationSubject_STT08() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Optional<Subject> subject = getSubject(!IS_STATUS);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(new Student()));
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(subject);

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest));

        Assertions.assertEquals(ErrorCode.COURSE_CLOSED_FOR_REGISTRATION, resultActual.getErrorCode());
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

    @Test
    void testCreateRegistrationSubject_STT09() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Optional<Subject> subject = getSubject(IS_STATUS);
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(subject);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(new Student()));
        Mockito.when(registrationSubjectRepository.insert(registrationSubject.getRegistrationSubjectID().getSubjectID(),registrationSubject.getRegistrationSubjectID().getStudentID(),registrationSubject.getUpdateBy(),SUBJECT_LIMIT_FOR_COURSE)).thenReturn(false);
        Mockito.when(globalValue.getSubjectLimitForCourse()).thenReturn(SUBJECT_LIMIT_FOR_COURSE);

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest));
        Assertions.assertEquals(ErrorCode.EXCEEDS_MAXIMUM_SUBJECTS, resultActual.getErrorCode());
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(registrationSubjectRepository).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

//    @Test
//    void testCreateRegistrationSubject_STT10() {
//        setUp(IS_ADMIN, null);
//        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
//        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
//        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(Optional.empty());
//        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_OWNER));
//
//        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest));
//        Assertions.assertEquals(ErrorCode.SUBJECT_ID_INVALID, resultActual.getErrorCode());
//        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
//        Mockito.verify(studentRepository).findById(STUDENT_ID);
//        Mockito.verify(registrationSubjectRepository, Mockito.never()).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(), registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
//        checkSecurityContextUse();
//    }

    @Test
    void testCreateRegistrationSubject_UNAUTHORIZED() {
        setUp(!IS_ADMIN, GMAIL_OWNER);
        RegistrationSubjectCreateRequest registrationSubjectCreateRequest = getRegistrationSubjectCreateRequest().build();
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_NOT_OWNER));

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.createRegistrationSubject(registrationSubjectCreateRequest));
        Assertions.assertEquals(ErrorCode.UNAUTHORIZED, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).insert(registrationSubject.getRegistrationSubjectID().getSubjectID(), registrationSubject.getRegistrationSubjectID().getStudentID(),registrationSubject.getUpdateBy(), SUBJECT_LIMIT_FOR_COURSE);
        checkSecurityContextUse();
    }

    @Test
    void testDeleteRegistrationSubject_STT01_ADMIN() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_ADMIN));
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(getSubject(IS_STATUS));
        Mockito.when(registrationSubjectRepository.deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID())).thenReturn(true);

        Boolean resultActual =  registrationCourseService.deleteRegistrationSubject(registrationSubjectDeleteRequest);
        Assertions.assertTrue(resultActual);
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository).deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
        checkSecurityContextUse();
    }

    @Test
    void testDeleteRegistrationSubject_STT01_STUDENT() {
        setUp(!IS_ADMIN, GMAIL_OWNER);
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_OWNER));
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(getSubject(IS_STATUS));
        Mockito.when(registrationSubjectRepository.deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID())).thenReturn(true);

        Boolean resultActual =  registrationCourseService.deleteRegistrationSubject(registrationSubjectDeleteRequest);
        Assertions.assertTrue(resultActual);
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository).deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
        checkSecurityContextUse();
    }

    @Test
    void testDeleteRegistrationSubject_STT02() {
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.deleteRegistrationSubject(registrationSubjectDeleteRequest));
        Assertions.assertEquals(ErrorCode.STU_ID_INVALID, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
    }

    @Test
    void testDeleteRegistrationSubject_STT03_ADMIN() {
        setUp(IS_ADMIN, null);
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_ADMIN));
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.deleteRegistrationSubject(registrationSubjectDeleteRequest));
        Assertions.assertEquals(ErrorCode.SUBJECT_ID_INVALID, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
        checkSecurityContextUse();
    }

    @Test
    void testDeleteRegistrationSubject_STT03_STUDENT() {
        setUp(!IS_ADMIN, GMAIL_OWNER);
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_OWNER));
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        Mockito.when(subjectRepository.searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED)).thenReturn(Optional.empty());

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.deleteRegistrationSubject(registrationSubjectDeleteRequest));
        Assertions.assertEquals(ErrorCode.SUBJECT_ID_INVALID, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
        checkSecurityContextUse();
    }

    @Test
    void testDeleteRegistrationSubject_UNAUTHORIZED() {
        setUp(!IS_ADMIN, GMAIL_OWNER);
        RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest = getRegistrationSubjectDeleteRequest().build();
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        Mockito.when(studentRepository.findById(STUDENT_ID)).thenReturn(getStudent(GMAIL_NOT_OWNER));

        AppException resultActual = Assertions.assertThrows(AppException.class, () -> registrationCourseService.deleteRegistrationSubject(registrationSubjectDeleteRequest));
        Assertions.assertEquals(ErrorCode.UNAUTHORIZED, resultActual.getErrorCode());
        Mockito.verify(studentRepository).findById(STUDENT_ID);
        Mockito.verify(subjectRepository, Mockito.never()).searchBySubjectIDAndIsDeleted(SUBJECT_ID, !IS_DELETED);
        Mockito.verify(registrationSubjectRepository, Mockito.never()).deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
    }

    private Optional<Subject> getSubject(boolean status){
        return Optional.of(
                Subject.builder()
                        .subjectID(SUBJECT_ID)
                        .subjectName("Công nghệ mới")
                        .startDate(LocalDate.now().plusDays(1))
                        .endDate(LocalDate.now().plusDays(30))
                        .numberOfStudent(30)
                        .numberOfCredit(3)
                        .courseID(COURSE_ID)
                        .status(status)
                        .tuition(400000L)
                        .description(null)
                        .isDeleted(!IS_DELETED)
                        .build()
        );
    }

    private Optional<Student> getStudent(String gmail){
        return Optional.of(
                Student.builder()
                        .email(gmail)
                        .build()
        );
    }

    private RegistrationSubjectID getRegistrationSubjectID(RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest){
        return  RegistrationSubjectID.builder()
                .subjectID(registrationSubjectDeleteRequest.getSubjectID())
                .studentID(registrationSubjectDeleteRequest.getStudentID())
                .build();
    }

    private void checkSecurityContextUse(){
        Mockito.verify(securityContext, Mockito.atMost(3)).getAuthentication();
        Mockito.verify(authentication, Mockito.atMost(3)).getPrincipal();
    }

    private RegistrationSubjectCreateRequest.RegistrationSubjectCreateRequestBuilder getRegistrationSubjectCreateRequest(){
        return RegistrationSubjectCreateRequest.builder()
                .subjectID(SUBJECT_ID)
                .studentID(STUDENT_ID);
    }
    private RegistrationSubjectDeleteRequest.RegistrationSubjectDeleteRequestBuilder getRegistrationSubjectDeleteRequest(){
        return RegistrationSubjectDeleteRequest.builder()
                .subjectID(SUBJECT_ID)
                .studentID(STUDENT_ID);
    }

    private RegistrationSubject getRegistrationSubject(RegistrationSubjectCreateRequest registrationSubjectCreateRequest) {
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectCreateRequest);
        return RegistrationSubject.builder()
                .registrationSubjectID(registrationSubjectID)
                .updateBy(getEmail())
                .build();
    }

    private RegistrationSubjectID getRegistrationSubjectID(RegistrationSubjectCreateRequest registrationSubjectCreateRequest) {
        return RegistrationSubjectID.builder()
                .subjectID(registrationSubjectCreateRequest.getSubjectID())
                .studentID(registrationSubjectCreateRequest.getStudentID())
                .build();
    }

    private String getEmail(){
        UserDetails userDetails = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
