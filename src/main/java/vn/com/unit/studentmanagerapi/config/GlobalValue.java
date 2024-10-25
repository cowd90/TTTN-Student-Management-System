package vn.com.unit.studentmanagerapi.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalValue {
    @Value("${student-manager.jwt.expiration}")
    Duration expiration;

    @Value("${student-manager.jwt.signer-key}")
    String signerKey;

    @Value("${student-manager.api.public}")
    String[] apiPublic;

    @Value("${student-manager.api.public-ant-matcher}")
    String[] apiPublicAntMatcher;

    @Value("${student-manager.paging.page}")
    int pageDefault;

    @Value("${student-manager.paging.size}")
    int sizeDefault;

    @Value("${student-manager.paging.sort-direction}")
    String sortDirection;

    @Value("${student-manager.subject.validation.subject-limit-for-course}")
    int subjectLimitForCourse ;
}
