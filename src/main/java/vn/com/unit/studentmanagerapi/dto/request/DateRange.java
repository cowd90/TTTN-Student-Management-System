package vn.com.unit.studentmanagerapi.dto.request;

import java.time.LocalDate;

public interface DateRange {
    LocalDate getStartDate();
    LocalDate getEndDate();
}
