package com.example.blogsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 统一处理需要登录验证的请求
 */
public class LoginInterceptor implements HandlerInterceptor {

	/**
	 * 在请求处理前进行拦截
	 * 检查 Session 中是否存在用户登录信息
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();

		// 检查是否已登录（Session 中是否存在 user 属性）
		if (session.getAttribute("user") == null) {
			// 未登录，重定向到登录页
			response.sendRedirect("/login");
			return false;
		}

		// 已登录，允许访问
		return true;
	}
}
