/*
 * Copyright (c) 2024 Weijue. All rights reserved.
 */
package com.example.blogsystem.controller;

import com.example.blogsystem.entity.Post;
import com.example.blogsystem.repository.PostRepository;
import com.example.blogsystem.util.MarkdownUtil;

import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 博客控制器，处理文章相关请求
 *
 * @author Weijue
 */
@Controller
public class BlogController {

    private final PostRepository postRepository;
    private final MarkdownUtil markdownUtil;

    /**
     * 构造函数
     *
     * @param postRepository 文章仓储
     * @param markdownUtil   Markdown 工具
     */
    public BlogController(PostRepository postRepository, MarkdownUtil markdownUtil) {
        this.postRepository = postRepository;
        this.markdownUtil = markdownUtil;
    }

    /**
     * 首页 - 显示所有文章
     *
     * @param model 视图模型
     * @return 首页模板
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        // 渲染 Markdown 为 HTML
        posts.forEach(post -> post.setRenderedContent(
                markdownUtil.markdownToHtml(post.getContent())));
        model.addAttribute("posts", posts);
        return "index";
    }

    /**
     * 登录页面
     *
     * @param authentication 认证信息
     * @return 登录模板或重定向
     */
    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin";
        }
        return "login";
    }

    /**
     * 管理页面
     *
     * @param model 视图模型
     * @return 管理模板
     */
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("posts", postRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createTime")));
        return "admin";
    }

    /**
     * 发布新文章
     *
     * @param title        文章标题
     * @param content      文章内容
     * @param authentication 认证信息
     * @return 重定向到管理页面
     */
    @PostMapping("/post")
    public String createPost(
            @RequestParam String title,
            @RequestParam String content,
            Authentication authentication) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(authentication == null ? "admin" : authentication.getName());
        postRepository.save(post);

        return "redirect:/admin";
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 重定向到管理页面
     */
    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return "redirect:/admin";
    }

    /**
     * 编辑文章页面
     *
     * @param id    文章ID
     * @param model 视图模型
     * @return 编辑模板
     */
    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在: " + id));
        model.addAttribute("post", post);
        return "edit";
    }

    /**
     * 更新文章
     *
     * @param id            文章ID
     * @param title         新标题
     * @param content       新内容
     * @param authentication 认证信息
     * @return 重定向到管理页面
     */
    @PostMapping("/post/update/{id}")
    public String updatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            Authentication authentication) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在: " + id));

        post.setTitle(title);
        post.setContent(content);
        if (authentication != null) {
            post.setAuthor(authentication.getName());
        }
        postRepository.save(post);

        return "redirect:/admin";
    }
}
