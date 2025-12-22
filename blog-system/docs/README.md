# 项目文档

本文档目录包含博客系统的所有技术文档和参考资料。

## 📚 文档列表

### 核心文档

| 文档 | 说明 |
|------|------|
| [API_DOCUMENTATION.md](API_DOCUMENTATION.md) | **API 接口文档** - 完整的 REST API 参考手册 |
| [BACKEND_PHASE_PLAN.md](BACKEND_PHASE_PLAN.md) | 后端开发计划 - 分阶段开发指南 |
| [FRONTEND_PROMPT.md](FRONTEND_PROMPT.md) | 前端开发提示 - 前端实现参考 |
| [HELP.md](HELP.md) | 帮助文档 - Spring Boot 参考资料 |

---

## 🚀 快速开始

### API 文档使用

1. **启动应用**：
   ```bash
   ./mvnw spring-boot:run
   ```

2. **查看 API 文档**：打开 [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

3. **接口测试**：使用 `api-test.http` 文件进行接口测试

### 默认账号

开发环境（`dev` profile）默认：

- **用户名**: admin
- **密码**: 123456

生产环境（`prod` profile）请通过环境变量配置：

- `BLOG_ADMIN_USERNAME`（可选，默认 `admin`）
- `BLOG_ADMIN_PASSWORD`（必填）

---

## 🐘 本地 PostgreSQL（可选）

### 方案 A：Homebrew（推荐）

```bash
brew install postgresql@16
brew services start postgresql@16
```

### 方案 B：Docker Compose

仓库根目录提供了 `docker-compose.yml`，可一键启动本地 PostgreSQL：

```bash
docker compose up -d
```

启动应用并连接本地 PostgreSQL（使用 `prod` profile）：

```bash
SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/blog" \
SPRING_DATASOURCE_USERNAME="blog" \
SPRING_DATASOURCE_PASSWORD="blogpass" \
BLOG_ADMIN_PASSWORD="123456" \
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

> 数据库 schema 会在应用启动时由 Flyway 自动创建/升级。

---

## 📁 文档结构

```
docs/
├── README.md                    # 本文档
├── API_DOCUMENTATION.md         # API 文档
├── BACKEND_PHASE_PLAN.md        # 后端开发计划
├── FRONTEND_PROMPT.md          # 前端开发提示
└── HELP.md                     # 帮助文档
```

---

## 🔗 相关链接

- **项目根目录**: `/Users/weijue/Code/Project/My_Blog/blog-system/`
- **API 测试文件**: `/Users/weijue/Code/Project/My_Blog/blog-system/api-test.http`
- **H2 控制台**: http://localhost:8080/h2-console
