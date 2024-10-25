package vn.com.unit.studentmanagerapi.service.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.unit.studentmanagerapi.config.GlobalValue;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectDeleteRequest;
import vn.com.unit.studentmanagerapi.entity.RegistrationSubject;
import vn.com.unit.studentmanagerapi.entity.RegistrationSubjectID;
import vn.com.unit.studentmanagerapi.entity.Student;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.entity.enums.Role;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.repository.RegistrationSubjectRepository;
import vn.com.unit.studentmanagerapi.repository.StudentRepository;
import vn.com.unit.studentmanagerapi.repository.SubjectRepository;
import vn.com.unit.studentmanagerapi.service.SubjectRegistrationService;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class SubjectRegistrationServiceImpl implements SubjectRegistrationService {
    GlobalValue globalValue;
    RegistrationSubjectRepository registrationSubjectRepository;
    StudentRepository studentRepository;
    SubjectRepository subjectRepository;

    static boolean IS_DELETED = true;

    @Override
    @Transactional
    public Boolean createRegistrationSubject(RegistrationSubjectCreateRequest registrationSubjectCreateRequest) {
        checkRegistrationSubjectID(registrationSubjectCreateRequest);
        RegistrationSubject registrationSubject = getRegistrationSubject(registrationSubjectCreateRequest);

        boolean isInserted = registrationSubjectRepository.insert(
                registrationSubject.getRegistrationSubjectID().getSubjectID(),
                registrationSubject.getRegistrationSubjectID().getStudentID(),
                registrationSubject.getUpdateBy(),
                globalValue.getSubjectLimitForCourse()
        );

        if(!isInserted)
            throw new AppException(ErrorCode.EXCEEDS_MAXIMUM_SUBJECTS);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteRegistrationSubject(RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest) {
        checkRegistrationSubjectID(registrationSubjectDeleteRequest);
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectDeleteRequest);
        registrationSubjectRepository.deleteById(registrationSubjectID.getSubjectID(), registrationSubjectID.getStudentID());
        return true;
    }

    private void checkRegistrationSubjectID(RegistrationSubjectCreateRequest registrationSubjectCreateRequest) {
        Student student = studentRepository
                .findById(registrationSubjectCreateRequest.getStudentID())
                .orElseThrow(() -> new AppException(ErrorCode.STU_ID_INVALID));
        checkAuthorization(student); // check quyền
        Subject subject = subjectRepository
                .searchBySubjectIDAndIsDeleted(registrationSubjectCreateRequest.getSubjectID(), !IS_DELETED)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_ID_INVALID));
        if (!subject.isStatus()) // Môn học còn cho đăng ký không
            throw new AppException(ErrorCode.COURSE_CLOSED_FOR_REGISTRATION);
    }

    private void checkRegistrationSubjectID(RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest) {
        Student student = studentRepository
                .findById(registrationSubjectDeleteRequest.getStudentID())
                .orElseThrow(() -> new AppException(ErrorCode.STU_ID_INVALID));
        checkAuthorization(student);
        Subject subject = subjectRepository
                .searchBySubjectIDAndIsDeleted(registrationSubjectDeleteRequest.getSubjectID(), !IS_DELETED)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_ID_INVALID));
    }

    private void checkAuthorization(Student student) {
        UserDetails userDetails = getUserDetails();
        if(Role.ADMIN.equals(getRole(userDetails)) || student.getEmail().equals(getEmail(userDetails))) {}
        else throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    private RegistrationSubject getRegistrationSubject(RegistrationSubjectCreateRequest registrationSubjectCreateRequest) {
        RegistrationSubjectID registrationSubjectID = getRegistrationSubjectID(registrationSubjectCreateRequest);
        return RegistrationSubject.builder()
                .registrationSubjectID(registrationSubjectID)
                .updateBy(getEmail())
                .build();
    }

    private RegistrationSubjectID getRegistrationSubjectID(RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest) {
        return RegistrationSubjectID.builder()
                .subjectID(registrationSubjectDeleteRequest.getSubjectID())
                .studentID(registrationSubjectDeleteRequest.getStudentID())
                .build();
    }

    private RegistrationSubjectID getRegistrationSubjectID(RegistrationSubjectCreateRequest registrationSubjectCreateRequest) {
        return RegistrationSubjectID.builder()
                .subjectID(registrationSubjectCreateRequest.getSubjectID())
                .studentID(registrationSubjectCreateRequest.getStudentID())
                .build();
    }

    private UserDetails getUserDetails(){
        return (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getEmail(){
        return getUserDetails().getUsername();
    }

    private String getEmail(UserDetails userDetails){
        return userDetails.getUsername();
    }

    private Role getRole(UserDetails userDetails){
        GrantedAuthority grantedAuthority = userDetails
                .getAuthorities()
                .stream()
                .findFirst()
                .get();
        return Role.valueOf(grantedAuthority.getAuthority());
    }
}
