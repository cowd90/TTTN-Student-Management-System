package vn.com.unit.studentmanagerapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import vn.com.unit.studentmanagerapi.dto.response.ApiResponse;
import vn.com.unit.studentmanagerapi.entity.Subject;

import java.util.Optional;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Student Manager API", version = "v1"),
        servers = {
                @Server(url = "/", description = "Default Server URL"),
                @Server(url = "/api/v1", description = "API v1 Server URL")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
        public static class ApiResponseBoolean extends ApiResponse<Boolean> {}
        public static class ApiResponseOptionalSubject extends ApiResponse<Optional<Subject>> {}
        public static class ApiResponsePageSubject extends ApiResponse<Page<Subject>> {}
        public static class ApiResponseString extends ApiResponse<String> {}
}
