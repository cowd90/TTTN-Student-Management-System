package vn.com.unit.studentmanagerapi.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;
import vn.com.unit.studentmanagerapi.entity.Student;
import vn.com.unit.studentmanagerapi.entity.enums.Gender;
import vn.com.unit.studentmanagerapi.util.MyImageUtil;

import java.time.LocalDateTime;

@Component
public abstract class StudentMapperDecorator implements StudentMapper {

    @Autowired
    private StudentMapper delegate;

    @Override
    public Student toStudent(StudentCreateRequest request) {
        // replace 1 space if there are many spaces between words
        String fullName = request.getFullName().trim().replaceAll("\\s{2,}", " ");

        // map request to student entity
        Student student = delegate.toStudent(request);

        student.setFullName(fullName);
        student.setEmail(request.getEmail().trim());
        student.setGender(Gender.valueOf(
                request.getGender().trim().toUpperCase())
                .getCode());

        return student;
    }

    @Override
    public StudentResponse toStudentResponse(Student student) {
        // map student entity to response
        StudentResponse response = delegate.toStudentResponse(student);

        if (student.getAvatar() != null) response.setAvatar(MyImageUtil.convertToBase64(student.getAvatar()));

        if (student.getGender() != null) {
            response.setGender(Gender.fromCode(student.getGender()).toString());
        }

        return response;
    }

    @Override
    public Student updateStudent(Student student, StudentUpdateRequest request) {
        Student updatedStudent = delegate.updateStudent(student, request);
        student.setGender(Gender.valueOf(request.getGender().trim().toUpperCase()).getCode());
        student.setUpdateAt(LocalDateTime.now());

        return updatedStudent;
    }
}
