/*
 * Copyright (c) 2024 Weijue. All rights reserved.
 */
package com.example.blogsystem.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 *
 * @author Weijue
 */
@Configuration
@EnableConfigurationProperties(AdminUserProperties.class)
public class SecurityConfig {

    /**
     * 创建密码编码器
     *
     * @return BCrypt 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 创建用户详情服务
     *
     * @param properties    管理员配置属性
     * @param passwordEncoder 密码编码器
     * @return 用户详情服务
     */
    @Bean
    public UserDetailsService userDetailsService(
            AdminUserProperties properties,
            PasswordEncoder passwordEncoder) {
        String username = (properties.username() == null
                || properties.username().isBlank()) ? "admin" : properties.username();
        String rawPassword = properties.password();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalStateException("缺少配置：app.security.admin.password");
        }

        UserDetails admin = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * 创建安全过滤链配置
     *
     * @param http HttpSecurity 配置
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/error", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/admin", "/post/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}
