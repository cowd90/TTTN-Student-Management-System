package vn.com.unit.studentmanagerapi.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.com.unit.studentmanagerapi.annotation.constraint.PasswordConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AccountUpdateRequest {
    @NotNull(message = "PASSWORD_NULL_EMPTY")
    @Size(min = 8, max = 100, message = "PASSWORD_LENGTH_INVALID")
    @PasswordConstraint(message = "PASSWORD_INVALID")
    String password;
}
