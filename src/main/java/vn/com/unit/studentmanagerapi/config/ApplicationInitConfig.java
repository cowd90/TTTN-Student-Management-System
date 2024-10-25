package vn.com.unit.studentmanagerapi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.enums.Role;
import vn.com.unit.studentmanagerapi.repository.AccountRepository;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "spring",
        value = "datasource.driver-class-name",
        havingValue = "org.postgresql.Driver")
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Value("${student-manager.account.admin-email}")
    private String ADMIN_EMAIL;

    @Value("${student-manager.account.admin-password}")
    private String ADMIN_PASSWORD;

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository) {
        return args -> {
            if (!accountRepository.existsByEmail(ADMIN_EMAIL)) {
                Account account = Account.builder()
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .role(Role.ADMIN.getCode())
                        .createdAt(LocalDateTime.now())
                        .updateAt(LocalDateTime.now())
                        .updateBy(ADMIN_EMAIL)
                        .isDeleted(false)
                        .build();

                accountRepository.insertUser(
                        account.getEmail(),
                        account.getPassword(),
                        account.getRole(),
                        account.getCreatedAt(),
                        account.getUpdateAt(),
                        account.getUpdateBy(),
                        account.isDeleted()
                );
                log.warn("Admin has been created with default " +
                        "email: {} and password: {}. Please change it", ADMIN_EMAIL, ADMIN_PASSWORD);
            }
        };
    }

}
