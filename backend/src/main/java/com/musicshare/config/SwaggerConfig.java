package com.musicshare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 *
 * <p>Configures Swagger UI for API documentation. Access the documentation at:
 * http://localhost:8081/swagger-ui.html
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI documentation
     *
     * @return OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", createSecurityScheme()));
    }

    /**
     * API information
     *
     * @return Info instance
     */
    private Info apiInfo() {
        return new Info()
                .title("MusicShare API Documentation")
                .description("RESTful API documentation for MusicShare music community platform")
                .version("1.0.0")
                .contact(new Contact()
                        .name("MusicShare Team")
                        .email("support@musicshare.com")
                        .url("https://musicshare.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    /**
     * Security scheme for JWT authentication
     *
     * @return SecurityScheme instance
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Enter JWT token (Bearer token will be added automatically)");
    }

}
