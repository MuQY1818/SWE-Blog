# Backend Phase 开发计划（先审阅后动工）

## 目标与约束（必须满足）

- **单体应用**：Spring Boot 3.x + Maven + Java 17，最终打包为单一可执行 JAR。
- **数据库**：`dev` 使用 **H2 内存库** `jdbc:h2:mem:blogdb`；`prod` 迁移到 **PostgreSQL**（外部实例）。
- **渲染模式**：Spring MVC，Controller **返回视图名 String**（不返回 JSON），后续由 Thymeleaf 渲染模板。
- **鉴权**：使用 **Spring Security**（表单登录 + CSRF），替代自定义 `HttpSession` 拦截器。

## 代码结构（按课程要求统一）

将后端代码统一到 `src/main/java/com/example/blogsystem/`：

- `entity/Post.java`
- `repository/PostRepository.java`
- `controller/BlogController.java`
- `config/WebConfig.java`（可选：拦截器）
- `BlogSystemApplication.java`（启动类）

> 说明：仓库历史上存在 `com.example.blog_system` 包名（含下划线）。如按课程结构要求，需要做一次 **包名与目录迁移**。
> ✅ 已执行：包名已迁移并统一为 `com.example.blogsystem`。

## 任务拆分（分阶段交付 + 分阶段测试）

### Phase 0：工程基线整理（一次性）

- 使用 Profile 拆分配置：`application-dev.properties`（H2）/ `application-prod.properties`（PostgreSQL）。
- 引入 Flyway 管理 schema（生产环境使用 `ddl-auto=validate`）。
- 清理不应提交的文件（例如 `src/main/java/com/.DS_Store`）。
- （可选但推荐）将 Spring Boot 版本从 `*-SNAPSHOT` 调整为稳定版，避免 CI 依赖解析不稳定（你确认后再改）。

**阶段测试**：`./mvnw -q test` 通过。

### Phase 1：核心领域模型与持久化

- 实现 `Post` 实体（`@Lob content`，`createTime` 通过 `@PrePersist` 自动写入）。
- 实现 `PostRepository extends JpaRepository<Post, Long>`。

**阶段测试**：新增最小化 `@DataJpaTest`（可选）或保留现有 contextLoads；`./mvnw -q test` 通过。

### Phase 2：Controller 核心路由（按需求逐条实现）

- 公共：`GET /`、`GET /login`
- 登录：`POST /login`（Spring Security 表单登录）、`POST /logout`
- 管理：`GET /admin`、`POST /post`、`POST /post/delete/{id}`
- 所有管理相关接口由 Spring Security 统一做鉴权 + CSRF 防护。

**阶段测试**：使用 `@SpringBootTest` + `MockMvc` 验证：

- 首页返回 view=`index` 且 model 含 `posts`
- 未登录访问 `/admin`、`/post`、`/post/delete/{id}` 会重定向到 `/login`
- 正确登录后可访问 `/admin`，发布/删除后按要求重定向

### Phase 3：（可选）拦截器统一鉴权

不再使用 `HandlerInterceptor` 做登录态拦截；改为 Spring Security 统一配置权限规则。

**阶段测试**：复用 Phase 2 的 MockMvc 用例。

## 提交与协作约定

- 我每完成一个 Phase 会提示你“可以提交”，并给出建议提交信息（例如 `feat: add post entity/repository`）。
- **你来提交**；我负责在提交前把测试跑通，并把运行命令与结果同步给你。

## 你需要先确认的 2 个问题

1. ✅ 已确认并完成：包名迁移到 `com.example.blogsystem`。
2. ✅ 已确认并完成：Spring Boot 父版本使用稳定版（已从 `*-SNAPSHOT` 调整为稳定版本）。
