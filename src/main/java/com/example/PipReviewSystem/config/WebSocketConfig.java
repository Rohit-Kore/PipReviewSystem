package com.example.PipReviewSystem.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration

@EnableWebSocketMessageBroker // ✅ Enables STOMP/WebSocket message broker

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override

    public void configureMessageBroker(MessageBrokerRegistry config) {

        // ✅ Enables in-memory message broker for broadcasting and user-specific messages

        config.enableSimpleBroker("/topic", "/queue", "/user"); // "queue" for private/user messages

        // ✅ Messages from client to server must be prefixed with /app

        config.setApplicationDestinationPrefixes("/app");

        // ✅ Used to send messages to specific users — enables /user/{username}/queue/...

        config.setUserDestinationPrefix("/user");

    }

    @Override

    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // ✅ Registers WebSocket endpoint ("/ws") for client connections

        // SockJS is a fallback option for browsers that don’t support native WebSocket

        registry.addEndpoint("/ws")

                .setAllowedOriginPatterns("*") // ✅ Allow all origins (adjust in production)

                .withSockJS();

    }

}

