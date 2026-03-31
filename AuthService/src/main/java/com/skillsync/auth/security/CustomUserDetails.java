package com.skillsync.auth.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.skillsync.auth.entity.User;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CustomUserDetails
 * DESCRIPTION:
 * Custom Spring Security UserDetails implementation that wraps 
 * the User entity for authentication purposes.
 * ================================================================
 */
public class CustomUserDetails implements UserDetails {

	private String email;
	private String password;
	private String role;
	private boolean enabled;

	public CustomUserDetails(User user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.role = user.getRole().name();
		this.enabled = user.getEnabled();
	}

    /* ================================================================
     * METHOD: getAuthorities
     * DESCRIPTION: Returns the user's roles as GrantedAuthority objects.
     * ================================================================ */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role));
	}

    /* ================================================================
     * METHOD: getPassword
     * DESCRIPTION: Returns the user's hashed password.
     * ================================================================ */
	@Override
	public String getPassword() {
		return password;
	}

    /* ================================================================
     * METHOD: getUsername
     * DESCRIPTION: Returns the user's email as the primary identifier.
     * ================================================================ */
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}