package com.skillsync.review.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.security.oauth2.jwt.Jwt;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: OpenApiConfig
 * DESCRIPTION:
 * Configures OpenAPI documentation for Review Service, including 
 * global security requirements for bearer authentication.
 * ================================================================
 */
@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(Jwt.class);
    }

    /* ================================================================
     * METHOD: customOpenAPI
     * DESCRIPTION: Returns the customized OpenAPI configuration for the service.
     * ================================================================ */
    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8092").description("API Gateway")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}