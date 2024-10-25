package vn.com.unit.studentmanagerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.com.unit.studentmanagerapi.dto.request.StudentCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.StudentUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.dto.response.StudentResponse;
import vn.com.unit.studentmanagerapi.service.StudentService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/students")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Quản lý sinh viên")
public class StudentController {

    StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Tạo thông tin sinh viên", description = "Chỉ admin mới có thể thực hiện")
    ApiResponse<StudentResponse> createStudent(@RequestBody @Valid StudentCreateRequest request) {
        return ApiResponse.<StudentResponse>builder()
                .data(studentService.createStudent(request))
                .build();
    }

    @PostMapping("upload/{email}")
    @PreAuthorize("hasAuthority('ADMIN') or principal.username == #email")
    @Operation(summary = "Upload ảnh")
    ApiResponse<StudentResponse> uploadPhoto(
            @PathVariable("email") String email,
            @RequestParam MultipartFile avatar
            )
    {
        return ApiResponse.<StudentResponse>builder()
                .data(studentService.uploadPhoto(email, avatar))
                .build();
    }

    @PutMapping("/{email}")
    @Operation(summary = "Cập nhật thông tin sinh viên")
    @PreAuthorize("hasAuthority('ADMIN') or principal.username == #email")
    ApiResponse<StudentResponse> updateStudent(
            @PathVariable("email") String email,
            @RequestBody @Valid StudentUpdateRequest request
            )
    {
        return ApiResponse.<StudentResponse>builder()
                .data(studentService.updateStudent(email, request))
                .build();
    }

    @GetMapping("/{email}")
    @Operation(summary = "Tìm kiếm một sinh viên bằng Email")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STUDENT')")
    ApiResponse<StudentResponse> getStudentByEmail(@PathVariable("email") String email) {
        return ApiResponse.<StudentResponse>builder()
                .data(studentService.findStudentByEmail(email))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sinh viên bằng ID", description = "Chỉ admin mới có thể thực hiện")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<String> deleteStudentById(@PathVariable("id") Long studentId) {
        return ApiResponse.<String>builder()
                .message(studentService.deleteStudentById(studentId))
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm nhiều sinh viên theo tên")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STUDENT')")
    ApiResponse<List<StudentResponse>> searchStudentByName(
            @RequestParam String term,
            @RequestParam(defaultValue = "${student-manager.paging.page}") int page,
            @RequestParam(defaultValue = "${student-manager.paging.size}") int size
    ) {
        return ApiResponse.<List<StudentResponse>>builder()
                .data(studentService.searchByName(term, page, size))
                .build();
    }

    @GetMapping("/search-by-date")
    @Operation(summary = "Tìm kiếm sinh viên theo ngày nhập học",
            description = "Chỉ admin mới có thể thực hiện")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<List<StudentResponse>> searchStudentByAdmissionDate(
            // TODO: global config
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "${student-manager.paging.page}") int page,
            @RequestParam(defaultValue = "${student-manager.paging.size}") int size
    ) {
        return ApiResponse.<List<StudentResponse>>builder()
                .data(studentService.searchByAdmissionDateRange(startDate, endDate, page, size))
                .build();
    }

    @GetMapping("/my-info")
    @Operation(summary = "Thông tin của tôi")
    ApiResponse<StudentResponse> getMyInfo() {
        return ApiResponse.<StudentResponse>builder()
                .data(studentService.getMyInfo())
                .build();
    }

}
