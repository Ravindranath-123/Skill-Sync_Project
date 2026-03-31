package com.skillsync.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
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
 * CLASS: SwaggerConfig
 * DESCRIPTION:
 * Configures Swagger/OpenAPI documentation settings for the Auth 
 * Service, including security schemes for JWT bearer tokens.
 * ================================================================
 */
@Configuration
public class SwaggerConfig {

    /* ================================================================
     * METHOD: customOpenAPI
     * DESCRIPTION: Returns the customized OpenAPI configuration for the service.
     * ================================================================ */
    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SkillSync Auth Service API")
                        .version("1.0")
                        .description("Authentication APIs documentation"))

                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                .servers(List.of(new Server().url("http://localhost:8092").description("API Gateway")))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}