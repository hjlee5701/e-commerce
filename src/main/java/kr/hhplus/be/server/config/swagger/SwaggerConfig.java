package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig{

    @Bean
    public OpenAPI openAPI() {
        // API 기본 설정
        Info info = new Info()
                .title("E-Commerce API Document")
                .version("1.0")
                .description("E-Commerce API 문서입니다.")
                .contact(
                        new Contact().email("hjlee5701@gmail.com")
                );
        // JWT 인증 방식
        String jwtScheme = "jwtAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtScheme);

        Components components = new Components()
                .addSecuritySchemes(jwtScheme, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .scheme("Bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8080"))
                .components(components)
                .info(info)
                .addSecurityItem(securityRequirement);
    }
}
