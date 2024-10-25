package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectFieldSearch;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectSortField;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectSearchRequest {
    SubjectFieldSearch fieldSearch;
    String valueSearch;
    int page;
    int size;
    SubjectSortField sortField;
    Sort.Direction sortDirection;
}
