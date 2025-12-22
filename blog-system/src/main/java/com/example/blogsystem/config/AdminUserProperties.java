package com.example.blogsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.admin")
public record AdminUserProperties(String username, String password) {}

