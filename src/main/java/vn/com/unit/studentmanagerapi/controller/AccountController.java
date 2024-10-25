package vn.com.unit.studentmanagerapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.AccountUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.request.LoginRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.service.AccountService;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Quản lý tài khoản")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    AccountService accountService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    ApiResponse<String> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.<String>builder()
                .data(accountService.login(request))
                .build();
    }

    @PostMapping
    @Operation(summary = "Tạo tài khoản", description = "Chỉ admin mới có thể thực hiện")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountCreateRequest request) {
        return ApiResponse.<AccountResponse>builder()
                .data(accountService.createAccount(request))
                .build();
    }

    @PutMapping("/{email}")
    @Operation(summary = "Đổi mật khẩu")
    @PreAuthorize("hasAuthority('ADMIN') or principal.username == #email")
    ApiResponse<AccountResponse> changePassword(
            @PathVariable("email") String email,
            @RequestBody @Valid AccountUpdateRequest request
            )
    {
        return ApiResponse.<AccountResponse>builder()
                .data(accountService.updateAccount(email, request))
                .build();
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Xóa tài khoản", description = "Chỉ admin mới có thể thực hiện")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<String> deleteAccount(@PathVariable("email") String email) {
        return ApiResponse.<String>builder()
                .message(accountService.deleteAccount(email))
                .build();
    }


}
