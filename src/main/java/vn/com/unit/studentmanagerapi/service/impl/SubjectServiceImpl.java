package vn.com.unit.studentmanagerapi.service.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectFieldSearch;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectSearchRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectUpdateRequest;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;
import vn.com.unit.studentmanagerapi.entity.enums.Role;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.repository.SubjectRepository;
import vn.com.unit.studentmanagerapi.service.SubjectService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class SubjectServiceImpl implements SubjectService {
    SubjectRepository subjectRepository;

    static final boolean IS_DELETED = true;

    @Override
    @Transactional
    public Boolean createSubject(SubjectCreateRequest subjectCreateRequest) {
        Subject subject = fromRequest(subjectCreateRequest)
                .updateBy(getEmail())
                .build();
        subjectRepository.save(subject); // lỗi DuplicateKeyException
        return true;
    }

    @Override
    @Transactional
    public Boolean updateSubject(Long subjectID, SubjectUpdateRequest subjectUpdateRequest) {
        Subject subject = subjectRepository
                .findById(subjectID)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        subject.setSubjectName(subjectUpdateRequest.getSubjectName());
        subject.setStartDate(subjectUpdateRequest.getStartDate());
        subject.setEndDate(subjectUpdateRequest.getEndDate());
        subject.setNumberOfCredit(subjectUpdateRequest.getNumberOfCredit());
        subject.setStatus(subjectUpdateRequest.isStatus());
        subject.setCourseID(subjectUpdateRequest.getCourseID());
        subject.setTuition(subjectUpdateRequest.getTuition());
        subject.setDescription(subjectUpdateRequest.getDescription());
        subject.setUpdatedAt(LocalDateTime.now());
        subject.setUpdateBy(getEmail());

        subjectRepository.save(subject);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteSubject(Long subjectID) {
        return subjectRepository.updateIsDeletedBySubjectID(subjectID, LocalDateTime.now(), getEmail(), IS_DELETED);
    }

    @Override
    public Subject getBySubjectID(Long subjectID) { // trả về ko có thì fail
        Optional<Subject> subject = null;
        Role role = getRole();

        Function<Long, Optional<Subject>> optionSearch = getOptionsFind(role);
        if(optionSearch != null) subject = optionSearch.apply(subjectID); // lỗi ở security
        else throw new AppException(ErrorCode.UNAUTHORIZED);

        if(!subject.isPresent())
           throw new AppException(ErrorCode.SUBJECT_ID_INVALID);

        return subject.get();
    }

    @Override
    public Page<Subject> search(SubjectSearchRequest subjectSearchRequest) {
        Page<Subject> subjects;
        PageRequest pageable = PageRequest.of(
                subjectSearchRequest.getPage(),
                subjectSearchRequest.getSize(),
                Sort.by(
                        subjectSearchRequest.getSortDirection() == Sort.Direction.ASC
                                ? Sort.Order.asc(subjectSearchRequest.getSortField()+"")
                                : Sort.Order.desc(subjectSearchRequest.getSortField()+"")
                )
        );

        BiFunction<String, PageRequest, Page<Subject>> optionSearch = getOptionsSearch(subjectSearchRequest.getFieldSearch());
        if(optionSearch != null) subjects = optionSearch.apply(subjectSearchRequest.getValueSearch(), pageable);
        else subjects = findAll(pageable);

        return subjects;
    }

    private Function<Long, Optional<Subject>> getOptionsFind(Role role){
        Map<Role, Function<Long, Optional<Subject>>> optionsFind = new HashMap<>();

        optionsFind.put(Role.ADMIN, (id) -> subjectRepository.findById(id));
        optionsFind.put(Role.STUDENT, (id) -> subjectRepository.searchBySubjectIDAndIsDeleted(id, !IS_DELETED));

        return optionsFind.get(role);
    }

    private BiFunction<String, PageRequest, Page<Subject>> getOptionsSearch(SubjectFieldSearch fieldSearch) {
        Map<SubjectFieldSearch, BiFunction<String, PageRequest, Page<Subject>>> optionsSearch = new HashMap<>();

        optionsSearch.put(SubjectFieldSearch.COURSE_ID, this::searchByCourseID);
        optionsSearch.put(SubjectFieldSearch.STUDENT_ID, this::searchByStudentID);

        return optionsSearch.get(fieldSearch);
    }

    private Page<Subject> searchByStudentID(String valueSearch, PageRequest pageRequest) {
        List<Subject> subjects = subjectRepository.searchByStudentID(Long.valueOf(valueSearch));
        return new PageImpl<>(subjects, pageRequest, subjects.size());
    }

    private Page<Subject> findAll(PageRequest pageRequest) {
        Page<Subject> subjects;
        Role role = getRole();
        switch (role){
            case ADMIN:
                subjects = subjectRepository.findAll(pageRequest);
                break;
            default:
                subjects = subjectRepository.searchByIsDeleted(!IS_DELETED, pageRequest);
        }
        return subjects;
    }

    private Page<Subject> searchByCourseID(String valueSearch, PageRequest pageRequest) {
        Page<Subject> subjects;

        CourseID courseID = Stream.of(CourseID.values())
                .filter(c -> c.name().equals(valueSearch))
                .findFirst()
                .orElse(CourseID.OTHER);
        Role role = getRole();

        switch (role){
            case ADMIN:
                subjects = subjectRepository.searchByCourseID(courseID, pageRequest);
                break;
            default:
                subjects = subjectRepository.searchByCourseIDAndIsDeleted(courseID,!IS_DELETED, pageRequest);
        }
        return subjects;
    }

    private UserDetails getUserDetails(){
        return (Account) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getEmail(){
        return getUserDetails().getUsername();
    }

    private Role getRole(){
        GrantedAuthority grantedAuthority = getUserDetails()
                .getAuthorities()
                .stream()
                .findFirst()
                .get();
        return Role.valueOf(grantedAuthority.getAuthority());
    }

    public Subject.SubjectBuilder fromRequest(SubjectCreateRequest subjectCreateRequest) {
        return Subject.builder()
                .subjectName(subjectCreateRequest.getSubjectName())
                .startDate(subjectCreateRequest.getStartDate())
                .endDate(subjectCreateRequest.getEndDate())
                .numberOfStudent(subjectCreateRequest.getNumberOfStudent())
                .numberOfAvailability(subjectCreateRequest.getNumberOfStudent())
                .numberOfCredit(subjectCreateRequest.getNumberOfCredit())
                .courseID(subjectCreateRequest.getCourseID())
                .status(true)
                .tuition(subjectCreateRequest.getTuition())
                .description(subjectCreateRequest.getDescription())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now());
    }
}
