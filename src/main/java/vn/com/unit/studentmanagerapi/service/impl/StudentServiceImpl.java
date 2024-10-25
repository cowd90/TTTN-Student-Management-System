package vn.com.unit.studentmanagerapi.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;
import vn.com.unit.studentmanagerapi.entity.Student;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.mapper.StudentMapper;
import vn.com.unit.studentmanagerapi.repository.AccountRepository;
import vn.com.unit.studentmanagerapi.repository.StudentRepository;
import vn.com.unit.studentmanagerapi.service.AccountService;
import vn.com.unit.studentmanagerapi.service.StudentService;
import vn.com.unit.studentmanagerapi.util.MyImageUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentServiceImpl implements StudentService {

    StudentRepository studentRepository;
    AccountRepository accountRepository;
    AccountService accountService;
    StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentCreateRequest request) {
        Student student = studentMapper.toStudent(request);

        // Check if account already exists in account table
        // If no, create a new account
        if (accountRepository.existsByEmail(student.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EXISTS);
        }

        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new AppException(ErrorCode.DOB_IN_FUTURE);
        }

        if (request.getDateOfBirth().isAfter(request.getDateOfAdmission())) {
            throw new AppException(ErrorCode.DOA_BEFORE_DOB);
        }

        String emailAuth = getAuthEmail();

        // Create Account for student
        AccountCreateRequest accountCreateRequest =
                AccountCreateRequest.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .updateBy(emailAuth)
                        .build();
        accountService.createAccount(accountCreateRequest);

        student.setCreatedAt(LocalDateTime.now());
        student.setUpdateAt(LocalDateTime.now());
        student.setUpdateBy(emailAuth);

        student = studentRepository.save(student);

        return studentMapper.toStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(String email, StudentUpdateRequest request) {
        // Check if student exists in student table
        // If yes, retrieve and update
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.STU_NOT_EXIST));

        studentMapper.updateStudent(student, request);
        student.setUpdateBy(getAuthEmail());

        student = studentRepository.save(student);

        return studentMapper.toStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse uploadPhoto(String email, MultipartFile photo) {
        MyImageUtil.validateFile(photo);

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.STU_NOT_EXIST));

        // convert multipart file to bytes and save to db
        byte[] photoBytes = MyImageUtil.convertFileToBytes(photo);
        student.setAvatar(photoBytes);
        student.setUpdateBy(getAuthEmail());
        studentRepository.save(student);

        StudentResponse studentResponse = studentMapper.toStudentResponse(student);
        // convert bytes to base64
        studentResponse.setAvatar(MyImageUtil.convertToBase64(student.getAvatar()));

        return studentResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse findStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.STU_NOT_EXIST));

        return studentMapper.toStudentResponse(student);
    }

    @Override
    @Transactional
    public String deleteStudentById(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new AppException(ErrorCode.STU_NOT_EXIST);
        }

        boolean isDeleted = studentRepository.softDeleteByStudentID(studentId);

        if (!isDeleted) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return "Student has been deleted!";
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> searchByName(String condition, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.PAGE_SIZE_INVALID);
        }

        int offset = page * size;
        List<Student> students = studentRepository.findAllByFullName(condition, size, offset);

        return students.stream()
                .map(studentMapper::toStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> searchByAdmissionDateRange(LocalDate start, LocalDate end, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.PAGE_SIZE_INVALID);
        }

        if (end.isBefore(start)) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_START_DATE);
        }

        int offset = page * size;
        List<Student> students = studentRepository
                .findAllByAdmissionDate(start, end, size, offset);

        return students.stream()
                .map(studentMapper::toStudentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentResponse getMyInfo() {
        String auth = getAuthEmail();
        Student student = studentRepository.findByEmail(auth)
                .orElseThrow(() -> new AppException(ErrorCode.STU_NOT_EXIST));

        return studentMapper.toStudentResponse(student);
    }

    public String getAuthEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
