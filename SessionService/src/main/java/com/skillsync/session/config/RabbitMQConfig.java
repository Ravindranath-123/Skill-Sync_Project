package com.skillsync.session.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: RabbitMQConfig
 * DESCRIPTION:
 * Configures RabbitMQ queues and message converters for asynchronous 
 * session event processing.
 * ================================================================
 */
@Configuration
public class RabbitMQConfig {

    /* ================================================================
     * METHOD: sessionQueue
     * DESCRIPTION: Defines the durable queue for session events.
     * ================================================================ */
    @Bean
    public Queue sessionQueue() {
        return new Queue("session.queue", true);
    }
    
    /* ================================================================
     * METHOD: jsonMessageConverter
     * DESCRIPTION: Configures JSON serialization for AMQP messages.
     * ================================================================ */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
}