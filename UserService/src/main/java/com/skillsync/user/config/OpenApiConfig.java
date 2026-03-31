package com.skillsync.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: OpenApiConfig
 * DESCRIPTION:
 * Configures OpenAPI (Swagger) documentation for the User Service,
 * including security schemes for bearer authentication.
 * ================================================================
 */
@Configuration
public class OpenApiConfig {

        /* ================================================================
         * METHOD: userAPI
         * DESCRIPTION: Customizes the OpenAPI definition for the service.
         * ================================================================ */
        @Bean
        public OpenAPI userAPI() {

                final String schemeName = "bearerAuth";

                return new OpenAPI()
                                .info(new Info()
                                                .title("User Service API")
                                                .version("1.0"))
                                .servers(List.of(new Server().url("http://localhost:8092").description("API Gateway")))
                                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                                .schemaRequirement(schemeName,
                                                new SecurityScheme()
                                                                .name(schemeName)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT"));
        }
}