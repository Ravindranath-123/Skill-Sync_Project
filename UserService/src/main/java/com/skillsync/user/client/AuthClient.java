package com.skillsync.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: AuthClient
 * DESCRIPTION:
 * This interface acts as a Feign Client to communicate with the
 * Authentication Service. It provides a method to validate users
 * by making a REST call to the auth-service microservice.
 * It enables inter-service communication in a microservices
 * architecture using declarative REST clients.
 * ================================================================
 */


@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/auth/validate/{userId}")
    Boolean validateUser(@PathVariable("userId") Long userId);
}