package com.skillsync.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.skillsync.auth.entity.User;
import com.skillsync.auth.repository.UserRepository;
import com.skillsync.auth.security.CustomUserDetails;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CustomUserDetailsService
 * DESCRIPTION:
 * Implementation of Spring Security's UserDetailsService to bridge 
 * the database user entity with the security context.
 * ================================================================
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /* ================================================================
     * METHOD: loadUserByUsername
     * DESCRIPTION: Loads user-specific data used to authenticate against.
     * ================================================================ */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}