package com.skillsync.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: Review
 * DESCRIPTION:
 * JPA Entity representing a mentor review submitted by a learner.
 * ================================================================
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mentorId;

    private Long learnerId;

    private Long sessionId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}