package vn.com.unit.studentmanagerapi.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;
import vn.com.unit.studentmanagerapi.entity.Student;

@Mapper(componentModel = "spring")
@DecoratedWith(StudentMapperDecorator.class)
public interface StudentMapper {
    Student toStudent(StudentCreateRequest request);

    @Mapping(target = "avatar", ignore = true)
    StudentResponse toStudentResponse(Student student);

    Student updateStudent(@MappingTarget Student student, StudentUpdateRequest request);
}
