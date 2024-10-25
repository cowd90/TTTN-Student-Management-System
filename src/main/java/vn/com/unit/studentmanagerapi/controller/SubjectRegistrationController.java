package vn.com.unit.studentmanagerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.unit.studentmanagerapi.config.SwaggerConfig;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.RegistrationSubjectDeleteRequest;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.service.SubjectRegistrationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/subjects/register")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Đăng ký môn học")
public class SubjectRegistrationController {
    SubjectRegistrationService subjectRegistrationService;

    @PostMapping
    @Operation(
            summary = "Tạo đơn đăng ký môn học",
            description = "API này cho phép tạo đơn đăng ký môn học.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseBoolean.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Input không đúng theo quy định!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Tạo thất bại (Môn học đã kết thúc hoặc vượt quá số lượng môn học có thể đăng ký)!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
            }
    )
    public ApiResponse<Boolean> createRegistrationCourse(
           @RequestBody @Valid RegistrationSubjectCreateRequest registrationSubjectCreateRequest
    ){
        return ApiResponse.<Boolean>builder()
                .code("APP_SUCCESS")
                .message("Success")
                .data(subjectRegistrationService.createRegistrationSubject(registrationSubjectCreateRequest))
                .build();
    }

    @DeleteMapping
    @Operation(
            summary = "Xóa đơn đăng ký môn học",
            description = "API này cho xóa đơn đăng ký môn học. <br> Role: <br> &emsp;&emsp; **Student** là phải sinh viên đã đăng ký môn học. <br> &emsp;&emsp; **Admin** có thể tùy ý xóa.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Không tìm thấy token ở header!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseBoolean.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Input không đúng theo quy định!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server đăng gặp lỗi, vui lòng thử lại!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SwaggerConfig.ApiResponseString.class))),
            }
    )
    public ApiResponse<Boolean> deleteRegistrationSubject(
            @RequestBody @Valid RegistrationSubjectDeleteRequest registrationSubjectDeleteRequest
    ){
        return ApiResponse.<Boolean>builder()
                .code("APP_SUCCESS")
                .message("Success")
                .data(subjectRegistrationService.deleteRegistrationSubject(registrationSubjectDeleteRequest))
                .build();
    }

}
