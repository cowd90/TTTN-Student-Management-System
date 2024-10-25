package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginRequest {
    @NotEmpty(message = "EMAIL_NULL_EMPTY")
    String email;

    @NotEmpty(message = "PASSWORD_NULL_EMPTY")
    String password;
}
