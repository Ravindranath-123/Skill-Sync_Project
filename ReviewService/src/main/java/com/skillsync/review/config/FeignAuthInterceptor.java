package com.skillsync.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: FeignAuthInterceptor
 * DESCRIPTION:
 * Intercepts Feign client requests to propagate the Authorization 
 * bearer token from the current request context.
 * ================================================================
 */
@Configuration
public class FeignAuthInterceptor {

    /* ================================================================
     * METHOD: requestInterceptor
     * DESCRIPTION: Returns the RequestInterceptor bean for token propagation.
     * ================================================================ */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String token = request.getHeader("Authorization");

                if (token != null) {
                    requestTemplate.header("Authorization", token);
                }
            }
        };
    }
}