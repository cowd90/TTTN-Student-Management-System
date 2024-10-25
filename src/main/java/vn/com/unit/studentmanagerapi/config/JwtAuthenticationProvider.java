package vn.com.unit.studentmanagerapi.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationProvider implements AuthenticationProvider {
    JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication){
        if(authentication!=null){
            String token = (String) authentication.getCredentials();
            if(jwtTokenProvider.validateToken(token))
                return jwtTokenProvider.getAuthentication(token);
        }

        throw new BadCredentialsException("Invalid Token");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
