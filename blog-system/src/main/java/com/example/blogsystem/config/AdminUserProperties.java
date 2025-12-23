/*
 * Copyright (c) 2024 Weijue. All rights reserved.
 */
package com.example.blogsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 管理员用户配置属性类
 *
 * @author Weijue
 */
@ConfigurationProperties(prefix = "app.security.admin")
public record AdminUserProperties(String username, String password) {
}
