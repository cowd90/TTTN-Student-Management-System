package vn.com.unit.studentmanagerapi.service;

import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectDeleteRequest;

public interface SubjectRegistrationService {
    Boolean createRegistrationSubject(RegistrationSubjectCreateRequest registrationSubjectCreateRequest);
    Boolean deleteRegistrationSubject(RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest);
}
