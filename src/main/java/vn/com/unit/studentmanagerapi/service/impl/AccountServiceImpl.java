package vn.com.unit.studentmanagerapi.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.unit.studentmanagerapi.config.JwtTokenProvider;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.AccountUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.request.LoginRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.enums.Role;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;
import vn.com.unit.studentmanagerapi.mapper.AccountMapper;
import vn.com.unit.studentmanagerapi.repository.AccountRepository;
import vn.com.unit.studentmanagerapi.service.AccountService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        Account account = accountRepository.findById(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTS));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if(!isAuthenticated){
            throw new AppException(ErrorCode.EMAIL_PASSWORD_INCORRECT);
        }

        return jwtTokenProvider.createToken(account);
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        Account account = Account.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.STUDENT.getCode())
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .updateBy(request.getEmail())
                .isDeleted(false)
                .build();

        boolean isCreated = accountRepository.insertUser(
                account.getEmail(),
                passwordEncoder.encode(account.getPassword()),
                account.getRole(),
                account.getCreatedAt(),
                account.getUpdateAt(),
                account.getUpdateBy(),
                account.isDeleted()
        );

        if (!isCreated) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return accountMapper.toAccountResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(String email, AccountUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authEmail = authentication.getName();

        Account account = accountRepository.findById(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTS));

        account = accountMapper.updateAccount(account, request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setUpdateAt(LocalDateTime.now());
        account.setUpdateBy(authEmail);

        account = accountRepository.save(account);

        return accountMapper.toAccountResponse(account);
    }

    @Override
    @Transactional
    public String deleteAccount(String email) {
        boolean isDeleted = accountRepository.deleteAccountByEmail(email);

        if (!isDeleted) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return "Student and Account has been deleted";
    }
}
