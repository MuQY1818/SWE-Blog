package com.example.blogsystem.controller;

import com.example.blogsystem.entity.Post;
import com.example.blogsystem.repository.PostRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BlogController {

	private final PostRepository postRepository;

	public BlogController(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	// 公共区域

	/**
	 * 首页 - 显示所有文章
	 */
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("posts", postRepository.findAll());
		return "index";
	}

	/**
	 * 登录页面
	 */
	@GetMapping("/login")
	public String login(HttpSession session) {
		// 如果已登录，重定向到管理页
		if (session.getAttribute("user") != null) {
			return "redirect:/admin";
		}
		return "login";
	}

	// 登录逻辑

	/**
	 * 提交登录
	 */
	@PostMapping("/login")
	public String doLogin(
			@RequestParam String username,
			@RequestParam String password,
			HttpSession session) {
		// 硬编码校验：admin/123456
		if ("admin".equals(username) && "123456".equals(password)) {
			session.setAttribute("user", "admin");
			return "redirect:/admin";
		}
		// 登录失败，带错误参数返回
		return "redirect:/login?error";
	}

	/**
	 * 退出登录
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	// 管理区域

	/**
	 * 管理页面 - 检查登录状态
	 */
	@GetMapping("/admin")
	public String admin(HttpSession session, Model model) {
		// 检查登录状态
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		model.addAttribute("posts", postRepository.findAll());
		return "admin";
	}

	/**
	 * 发布文章 - 检查登录状态
	 */
	@PostMapping("/post")
	public String createPost(
			@RequestParam String title,
			@RequestParam String content,
			HttpSession session) {
		// 检查登录状态
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}

		// 创建并保存文章
		Post post = new Post();
		post.setTitle(title);
		post.setContent(content);
		post.setAuthor("admin");
		postRepository.save(post);

		return "redirect:/";
	}

	/**
	 * 删除文章 - 检查登录状态
	 */
	@GetMapping("/post/delete/{id}")
	public String deletePost(@PathVariable Long id, HttpSession session) {
		// 检查登录状态
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}

		// 删除文章
		postRepository.deleteById(id);
		return "redirect:/admin";
	}
}
