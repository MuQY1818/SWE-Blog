package com.example.blogsystem.controller;

import com.example.blogsystem.entity.Post;
import com.example.blogsystem.repository.PostRepository;
import com.example.blogsystem.util.MarkdownUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BlogController {

	private final PostRepository postRepository;
	private final MarkdownUtil markdownUtil;

	public BlogController(PostRepository postRepository, MarkdownUtil markdownUtil) {
		this.postRepository = postRepository;
		this.markdownUtil = markdownUtil;
	}

	// 公共区域

	/**
	 * 首页 - 显示所有文章
	 */
	@GetMapping("/")
	public String index(Model model) {
		List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
		posts.forEach(post -> post.setRenderedContent(markdownUtil.markdownToHtml(post.getContent())));
		model.addAttribute("posts", posts);
		return "index";
	}

	/**
	 * 登录页面
	 */
	@GetMapping("/login")
	public String login(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/admin";
		}
		return "login";
	}

	// 管理区域

	/**
	 * 管理页面
	 * 注意：访问权限由 Spring Security 统一控制
	 */
	@GetMapping("/admin")
	public String admin(Model model) {
		model.addAttribute("posts", postRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime")));
		return "admin";
	}

	/**
	 * 发布文章
	 * 注意：访问权限由 Spring Security 统一控制
	 */
	@PostMapping("/post")
	public String createPost(
			@RequestParam String title,
			@RequestParam String content,
			Authentication authentication) {
		// 创建并保存文章
		Post post = new Post();
		post.setTitle(title);
		post.setContent(content);
		post.setAuthor(authentication == null ? "admin" : authentication.getName());
		postRepository.save(post);

		return "redirect:/admin";
	}

	/**
	 * 删除文章
	 * 注意：访问权限由 Spring Security 统一控制
	 */
	@PostMapping("/post/delete/{id}")
	public String deletePost(@PathVariable Long id) {
		// 删除文章
		postRepository.deleteById(id);
		return "redirect:/admin";
	}

	/**
	 * 编辑文章页面
	 * 注意：访问权限由 Spring Security 统一控制
	 */
	@GetMapping("/post/edit/{id}")
	public String editPost(@PathVariable Long id, Model model) {
		// 获取文章详情
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("文章不存在: " + id));
		model.addAttribute("post", post);
		return "edit";
	}

	/**
	 * 更新文章
	 * 注意：访问权限由 Spring Security 统一控制
	 */
	@PostMapping("/post/update/{id}")
	public String updatePost(
			@PathVariable Long id,
			@RequestParam String title,
			@RequestParam String content,
			Authentication authentication) {
		// 获取现有文章
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("文章不存在: " + id));

		// 更新文章
		post.setTitle(title);
		post.setContent(content);
		if (authentication != null) {
			post.setAuthor(authentication.getName());
		}
		postRepository.save(post);

		return "redirect:/admin";
	}
}
