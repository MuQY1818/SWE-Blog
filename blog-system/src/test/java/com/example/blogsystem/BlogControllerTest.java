/*
 * Copyright (c) 2025 Weijue. All rights reserved.
 */
package com.example.blogsystem;

import com.example.blogsystem.entity.Post;
import com.example.blogsystem.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 博客控制器集成测试类
 *
 * @author Weijue
 */
@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    /**
     * 每个测试用例执行前清理数据库
     */
    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    /**
     * 测试文章创建和列表功能
     *
     * @throws Exception 测试异常
     */
    @Test
    void testCreateAndListPost() throws Exception {
        // 1. 发布文章
        mockMvc.perform(post("/post")
                        .param("title", "测试文章")
                        .param("content", "这是测试内容")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        // 2. 验证文章已创建
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("posts", hasSize(1)))
                .andExpect(model().attribute("posts", hasItem(
                        allOf(
                                hasProperty("title", is("测试文章")),
                                hasProperty("renderedContent", containsString("这是测试内容"))
                        )
                )));
    }

    /**
     * 测试文章编辑和更新功能
     *
     * @throws Exception 测试异常
     */
    @Test
    void testEditAndUpdatePost() throws Exception {
        // 1. 创建文章
        Post post = new Post();
        post.setTitle("原始标题");
        post.setContent("原始内容");
        post.setAuthor("admin");
        post = postRepository.save(post);

        Long postId = post.getId();

        // 2. 访问编辑页面
        mockMvc.perform(get("/post/edit/" + postId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("post", hasProperty("id", is(postId))));

        // 3. 更新文章
        mockMvc.perform(post("/post/update/" + postId)
                        .param("title", "更新后的标题")
                        .param("content", "更新后的内容")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        // 4. 验证更新成功
        mockMvc.perform(get("/admin")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("posts", hasSize(1)))
                .andExpect(model().attribute("posts", hasItem(
                        allOf(
                                hasProperty("title", is("更新后的标题")),
                                hasProperty("content", is("更新后的内容"))
                        )
                )));
    }

    /**
     * 测试文章删除功能
     *
     * @throws Exception 测试异常
     */
    @Test
    void testDeletePost() throws Exception {
        // 1. 创建文章
        Post post = new Post();
        post.setTitle("要删除的文章");
        post.setContent("内容");
        post.setAuthor("admin");
        post = postRepository.save(post);

        // 2. 删除文章
        mockMvc.perform(post("/post/delete/" + post.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        // 3. 验证文章已删除
        mockMvc.perform(get("/admin")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("posts", hasSize(0)));
    }

    /**
     * 测试管理页面需要登录验证
     *
     * @throws Exception 测试异常
     */
    @Test
    void testLoginRequiredForAdmin() throws Exception {
        // 未登录访问管理页面应重定向到登录页
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // 未登录访问发布文章应重定向到登录页
        mockMvc.perform(post("/post")
                        .param("title", "测试")
                        .param("content", "测试")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // 未登录访问编辑页面应重定向到登录页
        mockMvc.perform(get("/post/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * 测试登录成功
     *
     * @throws Exception 测试异常
     */
    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "123456")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    /**
     * 测试登录失败
     *
     * @throws Exception 测试异常
     */
    @Test
    void testLoginFailure() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "wrong")
                        .param("password", "wrong")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    /**
     * 测试登出功能
     *
     * @throws Exception 测试异常
     */
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    /**
     * 测试已登录用户访问登录页时重定向到管理页
     *
     * @throws Exception 测试异常
     */
    @Test
    void testLoginPageRedirectWhenAlreadyLoggedIn() throws Exception {
        mockMvc.perform(get("/login")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }
}
