package org.techhub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                // =====================================================
                // API INFO
                // =====================================================

                .info(new Info()

                        .title("Resource Booking System API")

                        .description(
                                "A secure RESTful Resource Booking System with JWT authentication, "
                                + "role-based access control, resource management, and reservation management."
                        )

                        .version("1.0.0")

                        .contact(new Contact()

                                .name("TechHub")
                                .url("https://techhub.org")
                                .email("support@techhub.org")
                        )

                        .license(new License()

                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                )

                // =====================================================
                // SECURITY SCHEME - JWT
                // =====================================================

                .components(new Components()

                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Enter JWT token obtained from /auth/login"
                                        )
                        )
                )

                // =====================================================
                // GLOBAL SECURITY
                // =====================================================

                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer JWT")
                );
    }
}
