package vn.com.unit.studentmanagerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import vn.com.unit.studentmanagerapi.config.GlobalValue;
import vn.com.unit.studentmanagerapi.config.SwaggerConfig;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectFieldSearch;
import vn.com.unit.studentmanagerapi.dto.enums.SubjectSortField;
import vn.com.unit.studentmanagerapi.dto.request.SubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectSearchRequest;
import vn.com.unit.studentmanagerapi.dto.request.SubjectUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.entity.Subject;
import vn.com.unit.studentmanagerapi.service.SubjectService;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Quản lý môn học")
public class SubjectController {
    GlobalValue globalValue;
    SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Tạo mới môn học",
            description = "API này cho phép tạo mới một môn học.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token có role không hợp lệ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseBoolean.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Input không đúng theo quy định!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),

            }
    )
    public ApiResponse<Boolean> createSubject(
           @RequestBody @Valid SubjectCreateRequest subjectCreateRequest
    ){
        return ApiResponse.<Boolean>builder()
                .message("Success")
                .data(subjectService.createSubject(subjectCreateRequest))
                .build();
    }

    @PutMapping("/{subjectID}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Cập nhật môn học",
            description = "API này cho phép cập nhật một môn học.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token có role không hợp lệ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseBoolean.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Input không đúng theo quy định!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
            }
    )
    public ApiResponse<Boolean> updateSubject(
            @PathVariable("subjectID") Long subjectID,
            @RequestBody @Valid SubjectUpdateRequest subjectUpdateRequest
    ){
        return ApiResponse.<Boolean>builder()
                .message("Success")
                .data(subjectService.updateSubject(subjectID, subjectUpdateRequest))
                .build();
    }

    @DeleteMapping("/{subjectID}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
            summary = "Xóa tạm thời môn học",
            description = "API này cho phép xóa tạm thời một môn học.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token có role không hợp lệ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseBoolean.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),

            }
    )
    public ApiResponse<Boolean> deleteSubject(
            @PathVariable("subjectID") Long subjectID
    ){
        return ApiResponse.<Boolean>builder()
                .message("Success")
                .data(subjectService.deleteSubject(subjectID))
                .build();
    }

    @GetMapping(value = "/{subjectID}", produces = "application/json; charset=UTF-8")
    @Operation(
            summary = "Lấy thông tin môn học",
            description = "API này cho phép lấy thông tin một môn học. <br> Role: <br> &emsp;&emsp; **Student** chỉ có thể lấy môn học chưa bị xóa mềm. <br> &emsp;&emsp; **Admin** có thể lấy môn học kể cả đã xóa mềm.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseOptionalSubject.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),

            }
    )
    public ApiResponse<Subject> getBySubjectID(
            @PathVariable("subjectID") Long subjectID
    ){
        return ApiResponse.<Subject>builder()
                .message("Success")
                .data(subjectService.getBySubjectID(subjectID))
                .build();
    }

    @GetMapping(value = "/search", produces = "application/json; charset=UTF-8")
    @Operation(
            summary = "Tìm kiếm các môn học",
            description = "API này cho phép tìm kiếm các môn học . <br> Role: <br> &emsp;&emsp; **Student** chỉ có thể lấy các môn học chưa bị xóa mềm. <br> &emsp;&emsp; **Admin** có thể lấy các môn học kể cả đã xóa mềm.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponsePageSubject.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),

            }
    )
    public ApiResponse<Page<Subject>> search(
            @RequestParam(value = "field", required = false) Optional<SubjectFieldSearch> subjectFieldSearch,
            @RequestParam(value = "value", required = false) Optional<String> valueSearch,
            @RequestParam(value = "page", required = false) Optional<Integer> page,
            @RequestParam(value = "size", required = false) Optional<Integer> size,
            @RequestParam(value = "sort-by", required = false) Optional<SubjectSortField> subjectSortField,
            @RequestParam(value = "sort-direction",required = false) Optional<Sort.Direction> sortDirection
    ){
        SubjectSearchRequest subjectSearchRequest = SubjectSearchRequest.builder()
                .fieldSearch(subjectFieldSearch.orElse(SubjectFieldSearch.DEFAULT))
                .valueSearch(valueSearch.orElse(""))
                .page(page.orElse(globalValue.getPageDefault()))
                .size(size.orElse(globalValue.getSizeDefault()))
                .sortField(subjectSortField.orElse(SubjectSortField.subjectID))
                .sortDirection(sortDirection.orElse(Sort.Direction.ASC))
                .build();

        return ApiResponse.<Page<Subject>>builder()
                .message("Success")
                .data(subjectService.search(subjectSearchRequest))
                .build();
    }

}
