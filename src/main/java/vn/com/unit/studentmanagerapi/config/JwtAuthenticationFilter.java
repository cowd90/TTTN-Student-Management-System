package vn.com.unit.studentmanagerapi.config;

import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    GlobalValue globalValue;
    JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("JwtAuthenticationFilter | requestURI: {}", requestURI);

        boolean match = Arrays.stream(globalValue.getApiPublic()).anyMatch(requestURI::startsWith);

        try {
            if(!match){
                String token = jwtTokenProvider.resolveToken(request);
                if(token != null && jwtTokenProvider.validateToken(token)){
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (RuntimeException e){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
