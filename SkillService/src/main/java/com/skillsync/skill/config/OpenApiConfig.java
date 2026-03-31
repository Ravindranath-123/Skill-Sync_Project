package com.skillsync.skill.config;

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
 * Configures Swagger/OpenAPI documentation for the Skill Service,
 * specifically setting up bearer token security requirements.
 * ================================================================
 */
@Configuration
public class OpenApiConfig {

    /* ================================================================
     * METHOD: skillAPI
     * DESCRIPTION: Defines the OpenAPI metadata and security schemes.
     * ================================================================ */
    @Bean
    public OpenAPI skillAPI() {

        final String schemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Skill Service API")
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