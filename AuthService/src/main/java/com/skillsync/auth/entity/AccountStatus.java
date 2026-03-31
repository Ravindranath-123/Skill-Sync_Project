package com.skillsync.auth.entity;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: AccountStatus
 * DESCRIPTION:
 * Enumeration of account states for access control (ACTIVE, BLOCKED, etc).
 * ================================================================
 */
public enum AccountStatus {

    PENDING,
    ACTIVE,
    REJECTED,
    BLOCKED
}