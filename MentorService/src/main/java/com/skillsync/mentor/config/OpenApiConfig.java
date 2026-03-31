package com.skillsync.mentor.config;

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
 * Configures OpenAPI documentation for the Mentor Service, with 
 * support for bearer token authentication.
 * ================================================================
 */
@Configuration
public class OpenApiConfig {

	/* ================================================================
	 * METHOD: mentorServiceAPI
	 * DESCRIPTION: Returns the customized OpenAPI configuration for the service.
	 * ================================================================ */
	@Bean
	public OpenAPI mentorServiceAPI() {

	    final String securitySchemeName = "bearerAuth";

	    return new OpenAPI()
	            .info(new Info()
	                    .title("SkillSync Mentor Service API")
	                    .version("1.0"))
	            .servers(List.of(new Server().url("http://localhost:8092").description("API Gateway")))
	            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
	            .schemaRequirement(securitySchemeName,
	                    new SecurityScheme()
	                            .name(securitySchemeName)
	                            .type(SecurityScheme.Type.HTTP)
	                            .scheme("bearer")
	                            .bearerFormat("JWT"));
	}
}