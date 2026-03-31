package com.skillsync.notification.event;

import lombok.*;

import java.time.LocalDateTime;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionEvent
 * DESCRIPTION:
 * Data object representing a session-related event used for 
 * asynchronous messaging via RabbitMQ.
 * ================================================================
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionEvent {

    private Long sessionId;
    private Long mentorId;
    private Long learnerId;
    private String status;
    private LocalDateTime sessionTime;
}