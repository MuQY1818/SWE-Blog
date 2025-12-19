package com.example.blogsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 注册拦截器和其他 Web 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * 注册拦截器
	 * 配置需要拦截的路径和排除的路径
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册登录拦截器
		registry.addInterceptor(new LoginInterceptor())
				// 拦截的路径：管理页面和文章操作
				.addPathPatterns("/admin", "/post", "/post/**")
				// 排除的路径：登录相关页面和登录提交
				.excludePathPatterns("/login", "/login/**", "/", "/logout");
	}
}
