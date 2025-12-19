# 前端开发任务 - 博客系统前端实现

## 项目概述

这是一个基于 Spring Boot 的**个人博客系统**，已完成全部后端开发，现需实现前端页面。后端采用 **Thymeleaf 模板引擎**，所有页面通过 Controller 返回视图名称，由 Thymeleaf 渲染。

## 技术栈

- **后端**：Spring Boot 3.3.0 + Spring MVC + Thymeleaf
- **前端技术**：HTML5 + CSS3 + JavaScript（原生或任意框架）
- **数据库**：H2 文件数据库（`jdbc:h2:file:./data/blogdb`）
- **端口**：8080

## 已完成的后端功能

### 1. 数据库模型

**Post 实体**（对应 posts 表）：
- `id` (Long): 主键，自增
- `title` (String): 文章标题
- `content` (String): 文章内容（大文本）
- `author` (String): 作者名称
- `createTime` (LocalDateTime): 创建时间

### 2. 核心路由与功能

| 路径 | 方法 | 功能 | 是否需要登录 |
|------|------|------|--------------|
| `/` | GET | 首页，显示所有文章 | ❌ 否 |
| `/login` | GET | 登录页面 | ❌ 否 |
| `/login` | POST | 提交登录 | ❌ 否 |
| `/logout` | GET | 退出登录 | ❌ 否 |
| `/admin` | GET | 管理页面，显示文章列表 | ✅ 是 |
| `/post` | POST | 发布新文章 | ✅ 是 |
| `/post/delete/{id}` | GET | 删除文章 | ✅ 是 |

### 3. 登录信息

- **用户名**：`admin`
- **密码**：`123456`
- 登录后 Session 保存：`session.setAttribute("user", "admin")`

### 4. 拦截器

所有管理页面（`/admin`、`/post`、`/post/delete/{id}`）都由 `LoginInterceptor` 统一检查登录状态，未登录会重定向到 `/login`。

## 前端页面需求

### 现有模板文件

以下文件已存在但为空，需实现：

```
src/main/resources/templates/
├── index.html    # 首页 - 显示文章列表
├── login.html    # 登录页面
└── admin.html    # 管理页面 - 发布/删除文章
```

### 页面详细需求

#### 1. **首页** (`index.html`)

**功能**：
- 显示所有文章的列表
- 每篇文章显示：标题、内容摘要、作者、创建时间
- 点击文章可展开/查看完整内容

**页面结构**：
```html
<h1>我的博客</h1>
<div th:each="post : ${posts}">
    <h2 th:text="${post.title}"></h2>
    <p th:text="${post.content}"></p>
    <small th:text="'作者: ' + ${post.author} + ' | ' + ${post.createTime}"></small>
</div>
```

#### 2. **登录页** (`login.html`)

**功能**：
- 用户名/密码输入框
- 登录失败显示错误提示
- 登录成功后自动跳转

**表单**：
```html
<form method="post" action="/login">
    <input type="text" name="username" placeholder="用户名" required>
    <input type="password" name="password" placeholder="密码" required>
    <button type="submit">登录</button>
    <div th:if="${param.error}">
        <p style="color: red;">用户名或密码错误</p>
    </div>
</form>
```

#### 3. **管理页** (`admin.html`)

**功能**：
- 发布新文章的表单（标题、内容）
- 显示所有文章列表，每篇有删除按钮
- 顶部有退出登录链接

**页面结构**：
```html
<h1>管理后台</h1>

<!-- 发布文章表单 -->
<form method="post" action="/post">
    <input type="text" name="title" placeholder="文章标题" required>
    <textarea name="content" placeholder="文章内容" required></textarea>
    <button type="submit">发布</button>
</form>

<!-- 文章列表 -->
<div th:each="post : ${posts}">
    <h2 th:text="${post.title}"></h2>
    <p th:text="${post.content}"></p>
    <a th:href="@{/post/delete/{id}(id=${post.id})}">删除</a>
</div>

<a href="/logout">退出登录</a>
```

## Thymeleaf 模板语法要点

1. **循环**：`th:each="post : ${posts}"`
2. **文本**：`th:text="${post.title}"`
3. **链接**：`th:href="@{/post/delete/{id}(id=${post.id})}"`
4. **参数**：`${param.error}`（URL 参数）
5. **条件**：`th:if="${posts.size() > 0}"`

## 运行方式

### 1. 启动后端

```bash
./mvnw spring-boot:run
```

或运行 JAR：
```bash
java -jar target/blog-system-0.0.1-SNAPSHOT.jar
```

### 2. 访问地址

- 博客首页：`http://localhost:8080/`
- 登录页面：`http://localhost:8080/login`
- 管理后台：`http://localhost:8080/admin`（需先登录）

### 3. H2 数据库控制台（可选）

用于调试数据：`http://localhost:8080/h2-console`

- JDBC URL：`jdbc:h2:file:./data/blogdb`
- 用户名：`sa`
- 密码：（空）

## 项目结构

```
src/
├── main/
│   ├── java/com/example/blogsystem/
│   │   ├── BlogSystemApplication.java    # 启动类
│   │   ├── entity/
│   │   │   └── Post.java                 # 文章实体
│   │   ├── repository/
│   │   │   └── PostRepository.java       # JPA 接口
│   │   ├── controller/
│   │   │   └── BlogController.java       # 控制器
│   │   └── config/
│   │       ├── LoginInterceptor.java     # 登录拦截器
│   │       └── WebConfig.java            # Web 配置
│   └── resources/
│       ├── application.properties        # 应用配置
│       ├── static/                       # 静态资源（CSS/JS/图片）
│       └── templates/                    # Thymeleaf 模板
│           ├── index.html                # 首页（待实现）
│           ├── login.html                # 登录页（待实现）
│           └── admin.html                # 管理页（待实现）
└── test/                                 # 测试文件
```

## 开发建议

1. **样式**：可使用 Bootstrap 或 Tailwind CSS 等框架加速开发
2. **交互**：使用 JavaScript 增强用户体验（如表单验证、AJAX）
3. **静态资源**：将 CSS/JS 文件放在 `src/main/resources/static/` 目录
4. **测试**：开发时建议使用浏览器开发者工具调试

## 注意事项

- 后端已实现所有功能，无需修改 Java 代码
- 前端只需实现 Thymeleaf 模板和静态资源
- 所有页面都有对应的 Controller 方法，model 中有需要的数据
- 拦截器会自动处理未登录访问管理页面的情况
- 数据库是 H2 文件数据库，数据会持久化保存

## 交付物

请实现：
1. `src/main/resources/templates/index.html` - 完整的首页
2. `src/main/resources/templates/login.html` - 完整的登录页
3. `src/main/resources/templates/admin.html` - 完整的管理页
4. `src/main/resources/static/` 目录下的 CSS/JS 文件（如有）
5. 确保所有页面样式美观，用户体验良好
