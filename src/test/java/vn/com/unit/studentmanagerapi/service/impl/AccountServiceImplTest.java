package vn.com.unit.studentmanagerapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import vn.com.unit.studentmanagerapi.config.JwtAuthenticationProvider;
import vn.com.unit.studentmanagerapi.config.JwtTokenProvider;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.AccountUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.request.LoginRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.mapper.AccountMapper;
import vn.com.unit.studentmanagerapi.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@TestPropertySource("/test.yml")
class AccountServiceImplTest {
    @Autowired
    AccountServiceImpl accountService;

    @MockBean
    AccountRepository accountRepository;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    AccountMapper accountMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtAuthenticationProvider jwtAuthenticationProvider;

    AccountCreateRequest accountCreateRequest;
    AccountUpdateRequest accountUpdateRequest;
    AccountResponse response;
    Account account;

    LoginRequest loginRequest;

    static String EMAIL = "account@gmail.com";
    static String PASSWORD = "12345678";

    @BeforeEach
    void initData() {

        LocalDateTime createAt = LocalDateTime.now();
        LocalDateTime updateAt = LocalDateTime.now();

        accountCreateRequest = AccountCreateRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .createdAt(createAt)
                .updateAt(updateAt)
                .updateBy(EMAIL)
                .build();

        accountUpdateRequest = AccountUpdateRequest.builder()
                .password("12345678")
                .build();

        response = AccountResponse.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .createdAt(createAt)
                .updateAt(updateAt)
                .role("STUDENT")
                .updateBy(EMAIL)
                .isDeleted(false)
                .build();

        account = Account.builder()
                .email(EMAIL)
                .password("encodedPassword")
                .role('T')
                .createdAt(createAt)
                .updateAt(updateAt)
                .updateBy(EMAIL)
                .isDeleted(false)
                .build();

        loginRequest = LoginRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

    }

    @Test
    void testLogin_validRequest_success() {
        String email = "account@example.com";
        String password = "12345678";
        String encodedPassword = "encodedPassword";
        String token = "jwtToken";

        account.setEmail(email);
        account.setPassword(encodedPassword);

        when(accountRepository.findById(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.createToken(account)).thenReturn(token);

        String actualToken = accountService.login(loginRequest);

        assertEquals(token, actualToken);

    }

    @Test
    void testLogin_emailNotExist_fail() {
        // Given
        when(accountRepository.findById(EMAIL)).thenReturn(Optional.empty());

        // When
        var exception = assertThrows(AppException.class,
                () -> accountService.login(loginRequest));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("ACCOUNT_NOT_EXISTS");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Email is not signed up yet");

    }

    @Test
    void testLogin_passwordIncorrect_fail() {
        // Given
        when(accountRepository.findById(EMAIL)).thenReturn(Optional.ofNullable(account));
        when(passwordEncoder.matches(loginRequest.getPassword(), PASSWORD)).thenReturn(false);

        // When
        var exception = assertThrows(AppException.class,
                () -> accountService.login(loginRequest));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("EMAIL_PASSWORD_INCORRECT");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Email or password is incorrect");

    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_validRequest_success() {
        when(accountRepository.existsByEmail(accountCreateRequest.getEmail())).thenReturn(false);
        // Mock the password encoding
        when(passwordEncoder.encode(any(String.class)))
                .thenReturn("encodedPassword");

        // Mock the repository to return true, indicating a successful insertion
        when(accountRepository.insertUser(
                any(String.class),
                any(String.class),
                any(Character.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(String.class),
                any(Boolean.class)
        )).thenReturn(true);

        AccountResponse expectedResponse = new AccountResponse();

        // Mock the account mapping
        when(accountMapper.toAccountResponse(any(Account.class)))
                .thenReturn(expectedResponse);

        // Call the service method
        AccountResponse actualResponse = accountService.createAccount(accountCreateRequest);

        // Assert the result
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testCreateAccount_uncategorizedError_fail() {
        // Mock the repository to return true
        when(accountRepository.existsByEmail(accountCreateRequest.getEmail())).thenReturn(false);
        when(accountRepository.insertUser(
                account.getEmail(),
                account.getPassword(),
                account.getRole(),
                account.getCreatedAt(),
                account.getUpdateAt(),
                account.getUpdateBy(),
                account.isDeleted()
        )).thenReturn(false);


        // When
        var exception = assertThrows(AppException.class,
                () -> accountService.createAccount(accountCreateRequest));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("UNCATEGORIZED_EXCEPTION");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Uncategorized error. Please try again");
    }
    
    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateAccount_notExist_fail() {
        // Given
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        var exception = assertThrows(AppException.class,
                () -> accountService.updateAccount(EMAIL, accountUpdateRequest));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("ACCOUNT_NOT_EXISTS");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Email is not signed up yet");

    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateAccount_validRequest_success() {
        // Setup test data
        String email = "test@example.com";
        String authEmail = "auth@example.com";
        Account existingAccount = new Account(); // Initialize with test data
        Account updatedAccount = new Account(); // Initialize with test data
        AccountUpdateRequest request = new AccountUpdateRequest(); // Initialize with test data

        // Mock the SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(authEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock the accountRepository
        when(accountRepository.findById(email)).thenReturn(Optional.of(existingAccount));
        when(accountMapper.updateAccount(existingAccount, request)).thenReturn(updatedAccount);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);
        when(accountMapper.toAccountResponse(updatedAccount)).thenReturn(new AccountResponse()); // Replace with your actual response

        // Call the service method
        response = accountService.updateAccount(email, request);

        // Verify the results
        assertNotNull(response);
        verify(accountRepository).findById(email);
        verify(accountMapper).updateAccount(existingAccount, request);
        verify(passwordEncoder).encode(request.getPassword());
        verify(accountRepository).save(updatedAccount);
        verify(accountMapper).toAccountResponse(updatedAccount);

        assertEquals(authEmail, updatedAccount.getUpdateBy());
        assertNotNull(updatedAccount.getUpdateAt());
        assertEquals("encodedPassword", updatedAccount.getPassword());

    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testUpdateAccount_accountNotExist_fail() {
        // Given
        when(accountRepository.findById(EMAIL)).thenReturn(Optional.empty());

        // When
        var exception = assertThrows(AppException.class,
                () -> accountService.updateAccount(EMAIL, accountUpdateRequest));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("ACCOUNT_NOT_EXISTS");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Email is not signed up yet");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testDeleteAccount_validRequest_success() {
        // Given
        when(accountRepository.deleteAccountByEmail(EMAIL)).thenReturn(true);

        // When
        String message = accountService.deleteAccount(EMAIL);

        // Then
        assertThat(message).isEqualTo("Student and Account has been deleted");
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testDeleteAccount_uncategorizedError_fail() {
        // Given
        when(accountRepository.deleteAccountByEmail(EMAIL)).thenReturn(false);

        // When
        var exception = assertThrows(AppException.class,
                () -> accountService.deleteAccount(EMAIL));

        // Then
        assertThat(exception.getErrorCode().getCode())
                .isEqualTo("UNCATEGORIZED_EXCEPTION");
        assertThat(exception.getErrorCode().getMessage())
                .isEqualTo("Uncategorized error. Please try again");
    }

}
