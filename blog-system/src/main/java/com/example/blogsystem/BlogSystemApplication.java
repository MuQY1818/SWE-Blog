package com.example.blogsystem;

import com.example.blogsystem.entity.Post;
import com.example.blogsystem.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class BlogSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogSystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(PostRepository postRepository) {
		return args -> {
			if (postRepository.count() == 0) {
				createPost(postRepository, "Hello World", "欢迎来到 Weijue 的博客。\n\n这是一个基于 Spring Boot 和 Thymeleaf 构建的极简博客系统。在这里，我们将探索代码的奥秘，分享生活的点滴。\n\n保持饥渴，保持愚蠢。", "Weijue");
				createPost(postRepository, "设计之美：少即是多", "在用户界面设计中，“少即是多”（Less is More）不仅是一种审美选择，更是一种功能性原则。\n\n通过减少视觉噪音，我们能够引导用户关注真正重要的内容。留白不是浪费空间，而是创造呼吸感。\n\n优秀的交互设计应该是隐形的，让用户在使用过程中感受不到“设计”的存在，只有流畅的体验。", "Weijue");
				createPost(postRepository, "Spring Boot 实战笔记", "Spring Boot 极大地简化了 Java 企业级应用的开发。\n\n1. **自动配置**：根据类路径下的依赖自动配置 Bean。\n2. **起步依赖**：一站式管理依赖版本。\n3. **Actuator**：提供生产级的监控功能。\n\n代码示例：\n```java\n@RestController\npublic class HelloController {\n    @GetMapping(\"/\")\n    public String hello() {\n        return \"Hello Spring Boot!\";\n    }\n}\n```", "Admin");
				createPost(postRepository, "关于未来", "未来不属于预言家，而属于创造者。\n\n我们正在经历技术爆炸的时代，AI、云计算、边缘计算正在重塑我们的世界。作为开发者，我们不仅是观察者，更是参与者。\n\n拥抱变化，终身学习。", "Weijue");
			}
		};
	}

	private void createPost(PostRepository repo, String title, String content, String author) {
		Post post = new Post();
		post.setTitle(title);
		post.setContent(content);
		post.setAuthor(author);
		// 稍微错开一点时间，让列表看起来自然
		post.setCreateTime(LocalDateTime.now().minusHours((long) (Math.random() * 48)));
		repo.save(post);
	}
}
