package com.skillsync.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.*;

import feign.RequestInterceptor;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: RabbitMQConfig
 * DESCRIPTION:
 * Configures RabbitMQ infrastructure for the Notification Service, 
 * matching the production queue topology.
 * ================================================================
 */
@Configuration
public class RabbitMQConfig {

    /* ================================================================
     * METHOD: sessionQueue
     * DESCRIPTION: Defines the session event queue for notification processing.
     * ================================================================ */
    @Bean
    public Queue sessionQueue() {
        return new Queue("session.queue", true);
    }
    
    /* ================================================================
     * METHOD: jsonMessageConverter
     * DESCRIPTION: Configures the message converter for JSON payload consumption.
     * ================================================================ */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    
}