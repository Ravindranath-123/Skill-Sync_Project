package com.skillsync.user.repository;

import com.skillsync.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: UserProfileRepository
 * DESCRIPTION:
 * Repository interface for managing UserProfile persistence.
 * ================================================================
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}