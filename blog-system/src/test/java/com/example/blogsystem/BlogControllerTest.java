package com.example.blogsystem;

import com.example.blogsystem.entity.Post;
import com.example.blogsystem.repository.PostRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepository;

	@BeforeEach
	void setUp() {
		// 清理数据库
		postRepository.deleteAll();
	}

	@Test
	void testCreateAndListPost() throws Exception {
		// 1. 发布文章
		mockMvc.perform(post("/post")
						.param("title", "测试文章")
						.param("content", "这是测试内容")
						.sessionAttr("user", "admin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));

		// 2. 验证文章已创建
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attribute("posts", hasSize(1)))
				.andExpect(model().attribute("posts", hasItem(
						allOf(
								hasProperty("title", is("测试文章")),
								hasProperty("content", containsString("这是测试内容"))
						)
				)));
	}

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
						.sessionAttr("user", "admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("edit"))
				.andExpect(model().attribute("post", hasProperty("id", is(postId))));

		// 3. 更新文章
		mockMvc.perform(post("/post/update/" + postId)
						.param("title", "更新后的标题")
						.param("content", "更新后的内容")
						.sessionAttr("user", "admin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin"));

		// 4. 验证更新成功
		mockMvc.perform(get("/admin")
						.sessionAttr("user", "admin"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("posts", hasSize(1)))
				.andExpect(model().attribute("posts", hasItem(
						allOf(
								hasProperty("title", is("更新后的标题")),
								hasProperty("content", is("更新后的内容"))
						)
				)));
	}

	@Test
	void testDeletePost() throws Exception {
		// 1. 创建文章
		Post post = new Post();
		post.setTitle("要删除的文章");
		post.setContent("内容");
		post.setAuthor("admin");
		post = postRepository.save(post);

		// 2. 删除文章
		mockMvc.perform(get("/post/delete/" + post.getId())
						.sessionAttr("user", "admin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin"));

		// 3. 验证文章已删除
		mockMvc.perform(get("/admin")
						.sessionAttr("user", "admin"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("posts", hasSize(0)));
	}

	@Test
	void testLoginRequiredForAdmin() throws Exception {
		// 未登录访问管理页面应重定向到登录页
		mockMvc.perform(get("/admin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		// 未登录访问发布文章应重定向到登录页
		mockMvc.perform(post("/post")
						.param("title", "测试")
						.param("content", "测试"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		// 未登录访问编辑页面应重定向到登录页
		mockMvc.perform(get("/post/edit/1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void testLoginSuccess() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "admin")
						.param("password", "123456"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin"));
	}

	@Test
	void testLoginFailure() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "wrong")
						.param("password", "wrong"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	void testLogout() throws Exception {
		mockMvc.perform(get("/logout")
						.sessionAttr("user", "admin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void testLoginPageRedirectWhenAlreadyLoggedIn() throws Exception {
		mockMvc.perform(get("/login")
						.sessionAttr("user", "admin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin"));
	}
}
