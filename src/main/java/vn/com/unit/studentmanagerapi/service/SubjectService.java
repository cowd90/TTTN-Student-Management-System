package vn.com.unit.studentmanagerapi.service;

import org.springframework.data.domain.Page;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectSearchRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectUpdateRequest;
import vn.com.unit.studentmanagerapi.entity.Subject;

import java.util.Optional;

public interface SubjectService {
    Boolean createSubject(SubjectCreateRequest subjectCreateRequest);
    Boolean updateSubject(Long subjectID, SubjectUpdateRequest subjectUpdateRequest);
    Boolean deleteSubject(Long subjectID);
    Subject getBySubjectID(Long subjectID);
    Page<Subject> search(SubjectSearchRequest subjectSearchRequest);
}
