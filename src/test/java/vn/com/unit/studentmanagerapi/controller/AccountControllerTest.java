package vn.com.unit.studentmanagerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.AccountUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.request.LoginRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.repository.AccountRepository;
import vn.com.unit.studentmanagerapi.service.AccountService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@TestPropertySource("/test.yml")
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    AccountRepository accountRepository;

    LoginRequest loginRequest;

    AccountCreateRequest accountCreateRequest;

    AccountUpdateRequest accountUpdateRequest;

    AccountResponse response;

    static String EMAIL = "email@gmail.com";
    static String PASSWORD = "12345678";

    @BeforeEach
    void initData() {

        loginRequest = LoginRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        accountCreateRequest = AccountCreateRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        accountUpdateRequest = AccountUpdateRequest.builder()
                .password("123456789")
                .build();

        response = AccountResponse.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    @Test
    void testLogin_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);

        when(accountService.login(loginRequest)).thenReturn("token");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/accounts/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"));
    }

    @Test
    void testLogin_emailNull_fail() throws Exception {
        loginRequest.setEmail(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);

        when(accountService.login(loginRequest)).thenReturn("token");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email cannot be null or empty"));
    }

    @Test
    void testLogin_emailEmpty_fail() throws Exception {
        loginRequest.setEmail("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);

        when(accountService.login(loginRequest)).thenReturn("token");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email cannot be null or empty"));
    }

    @Test
    void testLogin_passwordNull_fail() throws Exception {
        loginRequest.setPassword(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);

        when(accountService.login(loginRequest)).thenReturn("token");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password cannot be null or empty"));
    }

    @Test
    void testLogin_passwordEmpty_fail() throws Exception {
        loginRequest.setPassword("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);

        when(accountService.login(loginRequest)).thenReturn("token");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password cannot be null or empty"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_emailNull_fail() throws Exception {
        accountCreateRequest.setEmail(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email cannot be null or empty"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_emailEmpty_fail() throws Exception {
        accountCreateRequest.setEmail("");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email cannot be null or empty"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_emailInvalidFormat_fail() throws Exception {
        accountCreateRequest.setEmail("email@");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("EMAIL_INVALID_FORMAT"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Email format is invalid"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_passwordNull_fail() throws Exception {
        accountCreateRequest.setPassword(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password cannot be null or empty"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_passwordTooShort_fail() throws Exception {
        accountCreateRequest.setPassword("1234567");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_LENGTH_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must be at least 8 and maximum of 100 characters"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_passwordTooLong_fail() throws Exception {
        String longPassword = new String(new char[101]).replace("\0", "a");
        accountCreateRequest.setPassword(longPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_LENGTH_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must be at least 8 and maximum of 100 characters"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_passwordContainsAccentedChar_fail() throws Exception {
        accountCreateRequest.setPassword("ơâô123456");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("ACC_PASSWORD_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must not contain accented characters or space"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateAccount_passwordContainsSpace_fail() throws Exception {
        accountCreateRequest.setPassword(" 1 2 3 4 5 6 7 8 ");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountCreateRequest);

        when(accountService.createAccount(accountCreateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("ACC_PASSWORD_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must not contain accented characters or space"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testChangePassword_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountUpdateRequest);

        when(accountService.updateAccount(EMAIL, accountUpdateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testChangePassword_passwordNull_fail() throws Exception {
        accountUpdateRequest.setPassword(null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountUpdateRequest);

        when(accountService.updateAccount(EMAIL, accountUpdateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_NULL_EMPTY"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password cannot be null or empty"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testChangePassword_passwordTooShort_fail() throws Exception {
        accountUpdateRequest.setPassword("1234567");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountUpdateRequest);

        when(accountService.updateAccount(EMAIL, accountUpdateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_LENGTH_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must be at least 8 and maximum of 100 characters"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testChangePassword_passwordTooLong_fail() throws Exception {
        String longPassword = new String(new char[101]).replace("\0", "a");
        accountUpdateRequest.setPassword(longPassword);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountUpdateRequest);

        when(accountService.updateAccount(EMAIL, accountUpdateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("PASSWORD_LENGTH_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must be at least 8 and maximum of 100 characters"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testChangePassword_passwordContainsAccentedChar_fail() throws Exception {
        accountUpdateRequest.setPassword("ơâô123456");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountUpdateRequest);

        when(accountService.updateAccount(EMAIL, accountUpdateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("ACC_PASSWORD_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must not contain accented characters or space"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testChangePassword_passwordContainsSpace_fail() throws Exception {
        accountUpdateRequest.setPassword(" 1 2 3 4 5 6 7 ");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(accountUpdateRequest);

        when(accountService.updateAccount(EMAIL, accountUpdateRequest)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("ACC_PASSWORD_INVALID"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Password must not contain accented characters or space"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testDeleteStudentById_validRequest_success() throws Exception {
        when(accountService.deleteAccount(EMAIL)).thenReturn(anyString());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/accounts/{email}", EMAIL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value("APP_SUCCESS"))
        ;
    }

}
