# 博客系统 API 文档

## 基础信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: Spring Security 表单登录（Session Cookie）
- **默认账号（dev）**: admin / 123456
- **内容类型**: `application/x-www-form-urlencoded`
- **安全提示**: 已启用 CSRF，所有修改数据的请求（POST）需要携带 CSRF Token

---

## 公共接口（无需登录）

### 1. 获取首页

**接口**: `GET /`

**描述**: 获取所有文章列表，Markdown 内容自动转换为 HTML

**响应**: 返回 `index.html` 模板，包含文章列表

**示例**:
```bash
curl http://localhost:8080/
```

---

### 2. 获取登录页面

**接口**: `GET /login`

**描述**: 显示登录表单页面

**响应**: 返回 `login.html` 模板

**示例**:
```bash
curl http://localhost:8080/login
```

---

### 3. 提交登录

**接口**: `POST /login`

**描述**: 用户登录验证（Spring Security）

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | ✅ | 用户名（默认：admin） |
| password | String | ✅ | 密码（默认：123456） |

**响应**:
- 登录成功：重定向到 `/admin`
- 登录失败：重定向到 `/login?error`

**示例（需要先获取 CSRF Token）**:
```bash
# 1) 获取登录页并保存 Cookie
curl -c cookies.txt http://localhost:8080/login -o login.html

# 2) 从页面中提取 CSRF Token（默认字段名为 _csrf）
TOKEN=$(grep -o 'name=\"_csrf\" value=\"[^\"]*\"' login.html | sed 's/.*value=\"//;s/\"$//')

# 3) 提交登录
curl -b cookies.txt -c cookies.txt -X POST http://localhost:8080/login \
  -d "username=admin&password=123456&_csrf=${TOKEN}"
```

---

### 4. 退出登录

**接口**: `POST /logout`

**描述**: 退出登录（CSRF 默认开启，因此需使用 POST）

**响应**: 重定向到 `/login?logout`

**示例**:
```bash
# 需要带 Cookie + CSRF Token（可从任意页面表单中获取 _csrf）
curl -b cookies.txt -X POST http://localhost:8080/logout -d "_csrf=${TOKEN}"
```

---

## 需要登录的接口

> ⚠️ **注意**: 以下接口需要先登录获取 Session Cookie
>
> 登录后 Cookie: `JSESSIONID=xxxxxxxxxxxx`

### 5. 获取管理页面

**接口**: `GET /admin`

**描述**: 显示文章管理界面

**权限**: ✅ 需要登录

**响应**: 返回 `admin.html` 模板，包含文章列表和管理功能

**示例**:
```bash
curl -b cookies.txt http://localhost:8080/admin
```

---

### 6. 发布新文章

**接口**: `POST /post`

**描述**: 创建一篇新文章

**权限**: ✅ 需要登录

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | ✅ | 文章标题 |
| content | String | ✅ | 文章内容（支持 Markdown） |

> 作者将自动使用当前登录用户名。

**响应**: 重定向到管理页面 `/admin`

**示例**:
```bash
curl -X POST http://localhost:8080/post \
  -b cookies.txt \
  -d "title=测试文章&content=## 标题\n\n正文内容&_csrf=${TOKEN}"
```

---

### 7. 编辑文章页面

**接口**: `GET /post/edit/{id}`

**描述**: 显示文章编辑表单

**权限**: ✅ 需要登录

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | ✅ | 文章 ID |

**响应**: 返回 `edit.html` 模板，包含预填充的文章内容

**示例**:
```bash
curl -b cookies.txt http://localhost:8080/post/edit/1
```

---

### 8. 更新文章

**接口**: `POST /post/update/{id}`

**描述**: 更新指定文章的内容

**权限**: ✅ 需要登录

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | ✅ | 文章 ID |

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | ✅ | 文章标题 |
| content | String | ✅ | 文章内容（支持 Markdown） |

**响应**: 重定向到管理页面 `/admin`

**示例**:
```bash
curl -X POST http://localhost:8080/post/update/1 \
  -b cookies.txt \
  -d "title=更新后的标题&content=更新后的内容&_csrf=${TOKEN}"
```

---

### 9. 删除文章

**接口**: `POST /post/delete/{id}`

**描述**: 删除指定文章

**权限**: ✅ 需要登录

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | ✅ | 文章 ID |

**响应**: 重定向到管理页面 `/admin`

**示例**:
```bash
curl -b cookies.txt -X POST http://localhost:8080/post/delete/1 -d "_csrf=${TOKEN}"
```

---

## 数据库控制台

**H2 Console**: http://localhost:8080/h2-console

**连接参数**:
- **JDBC URL**: `jdbc:h2:mem:blogdb`
- **用户名**: `sa`
- **密码**: `(空)`

> 说明：H2 Console 仅在 `dev` profile 开启；`prod` profile 默认关闭。

---

## 生产环境（PostgreSQL）配置

启动时激活 `prod` profile：

```bash
SPRING_DATASOURCE_URL="jdbc:postgresql://<host>:5432/<db>" \
SPRING_DATASOURCE_USERNAME="<user>" \
SPRING_DATASOURCE_PASSWORD="<password>" \
BLOG_ADMIN_PASSWORD="<adminPassword>" \
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

> schema 由 Flyway 自动迁移（脚本位于 `src/main/resources/db/migration/`）。

---

## 完整测试示例

### 1. 使用 curl

```bash
# 步骤 1: 获取 CSRF + Cookie
curl -c cookies.txt http://localhost:8080/login -o login.html
TOKEN=$(grep -o 'name=\"_csrf\" value=\"[^\"]*\"' login.html | sed 's/.*value=\"//;s/\"$//')

# 步骤 2: 登录
curl -b cookies.txt -c cookies.txt -X POST http://localhost:8080/login \
  -d "username=admin&password=123456&_csrf=${TOKEN}"

# 步骤 3: 发布文章
curl -X POST http://localhost:8080/post \
  -b cookies.txt \
  -d "title=我的第一篇文章&content=## 欢迎\n\n这是使用 Markdown 编写的内容&_csrf=${TOKEN}"

# 步骤 4: 查看管理页面
curl -b cookies.txt http://localhost:8080/admin

# 步骤 5: 删除文章
curl -b cookies.txt -X POST http://localhost:8080/post/delete/1 -d "_csrf=${TOKEN}"

# 步骤 6: 退出登录
curl -b cookies.txt -X POST http://localhost:8080/logout -d "_csrf=${TOKEN}"
```

### 2. 使用 REST Client 插件

使用项目中的 `api-test.http` 文件，包含所有接口的预配置请求。

---

## 错误处理

| 状态码 | 场景 | 说明 |
|--------|------|------|
| 302 | 未登录访问受保护接口 | 自动重定向到 `/login` |
| 302 | 登录失败 | 重定向到 `/login?error` |
| 500 | 文章不存在 | 抛出 `IllegalArgumentException` |

---

## 权限规则（Spring Security）

受保护路径（需要 `ROLE_ADMIN`）：
- `/admin`
- `/post/**`

公开路径（无需登录）：
- `/`、`/login`、`/error`
- 静态资源：`/css/**`、`/js/**`

---

## 数据模型

### Post 实体

```json
{
  "id": 1,
  "title": "文章标题",
  "content": "Markdown 格式的文章内容",
  "author": "admin",
  "createTime": "2024-01-01T12:00:00"
}
```

---

## 注意事项

1. **Markdown 支持**: 文章内容支持 Markdown 语法，渲染为 HTML
2. **安全策略**: Markdown 渲染已开启 `escapeHtml` 与 `sanitizeUrls`，并且启用 CSRF
3. **作者字段**: 自动使用当前登录用户名
4. **数据库**: `dev` 使用 H2（内存）；`prod` 使用 PostgreSQL，并通过 Flyway 管理 schema
