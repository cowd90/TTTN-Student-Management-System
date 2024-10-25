package vn.com.unit.studentmanagerapi.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.entity.enums.CourseID;

import javax.validation.ConstraintViolation;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    private static final String MAX_ATTRIBUTE = "max";
    private static final String VALUE_ATTRIBUTE = "value";

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<ErrorCode>> handlingRuntimeException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<ErrorCode> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    ResponseEntity<ApiResponse<Object>> handlingDuplicateKey(DuplicateKeyException e) {
        ErrorCode errorCode = ErrorCode.ACCOUNT_EXISTS;

        if(Objects.requireNonNull(e.getMessage()).contains("bus_registration_subject")){
            errorCode = ErrorCode.STU_ALREADY_REGISTERED;

            return ResponseEntity.status(errorCode.getHttpStatus())
                    .body(ApiResponse.builder()
                            .code(errorCode.getCode())
                            .message(errorCode.getMessage())
                            .build());
        }


        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }


    @ExceptionHandler(value = InvalidFormatException.class)
    ResponseEntity<ApiResponse<Object>> handlingDateFormat() {
        ErrorCode errorCode = ErrorCode.DATE_INVALID_FORMAT;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(HttpMessageNotReadableException e) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        if(e.getCause() instanceof InvalidFormatException){
            InvalidFormatException cause = (InvalidFormatException) e.getCause();
            Class<?> targetType = cause.getTargetType();

            if(targetType.equals(CourseID.class))
                errorCode = ErrorCode.COURSE_ID_INVALID;
            if(targetType.equals(LocalDate.class))
                errorCode = ErrorCode.DATE_INVALID;
        }

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<ErrorCode>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getAllErrors()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION))
                .getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            ConstraintViolation<?> constraintViolation =
                    exception.getBindingResult()
                            .getAllErrors()
                            .stream().findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION))
                            .unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException: {}", e.getMessage());
        }

        ApiResponse<ErrorCode> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        String maxValue = String.valueOf(attributes.get(MAX_ATTRIBUTE));
        String value = String.valueOf(attributes.get(VALUE_ATTRIBUTE));

        return message
                .replace("{" + MIN_ATTRIBUTE + "}", minValue)
                .replace("{" + MAX_ATTRIBUTE + "}", maxValue)
                .replace("{" + VALUE_ATTRIBUTE + "}", value);
    }
}
