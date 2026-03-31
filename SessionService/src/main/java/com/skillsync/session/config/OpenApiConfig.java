package com.skillsync.session.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
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
 * Configures OpenAPI/Swagger documentation for the Session Service, 
 * including security scheme definitions and JWT wrapper ignorance.
 * ================================================================
 */
@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(Jwt.class);
    }

    /* ================================================================
     * METHOD: sessionAPI
     * DESCRIPTION: Returns the customized OpenAPI configuration bean.
     * ================================================================ */
    @Bean
    public OpenAPI sessionAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SkillSync Session Service")
                        .version("1.0")
                        .description("Mentor session booking APIs"))
                
                .servers(List.of(new Server().url("http://localhost:8092").description("API Gateway")))
                // ⭐ this line enables lock icon globally
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