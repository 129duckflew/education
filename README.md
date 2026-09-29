# 教授面对面（重构版）

毕业设计项目「教授面对面」的现代化重构：从 Spring Boot 2.5 + MyBatis-Plus + MySQL + Vue 2/ElementUI，
迁移到 **Spring Boot 4.1 + Java 25 + 虚拟线程 + PostgreSQL + Spring Data JPA + React/shadcn**。

> 旧代码完整保留在 `legacy` 分支，便于对照。

## 技术栈

| 层 | 技术 |
|---|---|
| 运行时 | Java 25（LTS）+ Spring Boot 4.1 + 虚拟线程（`spring.threads.virtual.enabled=true`） |
| 数据库 | PostgreSQL 16 + Flyway |
| ORM | Spring Data JPA（Hibernate 7） |
| 鉴权 | Spring Security 7 + JWT（HMAC-SHA256）+ BCrypt |
| 接口文档 | springdoc-openapi（Swagger UI） |
| 前端 | React 19 + Vite + TypeScript + Tailwind CSS v4 + shadcn 风格组件 |
| 前端数据 | TanStack Query + React Router |
| 测试 | JUnit 5 + Testcontainers(PostgreSQL) + MockMvc |

### 相对旧版的取舍（“更轻量”）

| 旧 | 新 | 说明 |
|---|---|---|
| MyBatis-Plus + XML 手写 SQL | Spring Data JPA | 实体/仓储 + 少量原生查询 |
| MySQL 5.7 | PostgreSQL 16 | 统一类型、外键、唯一约束、`pg_trgm` |
| Sa-Token + Redis 会话 | Spring Security + JWT | 无状态，移除 Redis |
| RabbitMQ | 数据库 outbox + `@Async` | 邮件/短信异步与重试，移除 MQ |
| Elasticsearch | PostgreSQL `pg_trgm` | 中文模糊检索，移除 ES/Kibana |
| MinIO | 本地磁盘 `StorageService` | 抽象可插拔，可换回对象存储 |
| 10 个 Maven 模块 | 单个模块化单体 | 按业务 feature 纵向分包 |
| Vue + ElementUI | React + shadcn | 单应用，公共站 + `/admin` |

## 目录结构

```
education/
├── backend/                      # 单个 Spring Boot 应用
│   ├── src/main/java/cn/duckflew/education/
│   │   ├── common/               # 统一响应、异常、审计基类、outbox、通知、配置
│   │   ├── security/             # JWT + Spring Security + CurrentUser
│   │   ├── user/ professor/ qa/  # 业务域（entity/repository/service/controller/dto）
│   │   ├── taxonomy/ guide/ resource/ news/ messaging/ order/ file/ search/
│   │   └── admin/                # 后台 RBAC 与权限解析
│   ├── src/main/resources/db/migration/   # Flyway V1 schema / V2 种子
│   └── scripts/test.sh           # 测试脚本（colima 环境变量）
├── frontend/                     # React + Vite 单应用
│   └── src/{app,components,pages,lib}
├── docker-compose.yml
└── legacy 分支：旧版 10 模块单体
```

## 快速开始

### 1. 准备数据库

本机已安装 PostgreSQL 16 并运行在 `localhost:5432`：

```bash
createdb education
```

连接信息通过环境变量覆盖（默认 `DB_USER=liang`，无密码）：
`backend/src/main/resources/application-local.yml`。

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run          # 默认 local profile，端口 8080
```

- Flyway 会自动建表并写入字典/角色种子数据。
- 首次启动会创建默认管理员：**admin / admin123**（`app.bootstrap.admin.*` 可配置）。
- Swagger UI：http://localhost:8080/swagger-ui.html

### 3. 启动前端

```bash
cd frontend
pnpm install
pnpm dev                     # http://localhost:5173，/api 代理到 8080
```

## 运行测试

```bash
cd backend
./scripts/test.sh
```

`scripts/test.sh` 会自动为 Testcontainers 设置 colima 的 Docker socket。

> 使用 colima 时需先启动 docker 运行时：`colima start --profile docker`。
> Testcontainers 依赖 `DOCKER_HOST` 与 `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE`（脚本已处理），
> 并在 surefire 中设置 `api.version=1.44` 以兼容 Docker Engine 29。

## 初期数据

Flyway 迁移按顺序构建初期数据：

| 迁移 | 内容 |
|---|---|
| `V1__schema.sql` | 全量 schema |
| `V2__reference_data.sql` | 职称/学位字典、后台角色权限 |
| `V3__reference_data.sql` | **从旧系统 `sql/edu.sql` 抽取转换**：2631 所学校、456 条专业目录、149 个咨询领域（点号 id 转 `parent_id`） |
| `V4__demo_content.sql` | 原创演示内容：5 个演示账号、教授资料/教育经历/可答领域、10 条问答、3 条资讯、学习指南树 |
| `V5__demo_interests.sql` | 演示学生的关注领域（用于验证推荐） |

**演示账号**（密码均为 `password123`；管理员由应用启动时创建 `admin / admin123`）：

| 账号 | 角色 | 领域 |
|---|---|---|
| `student@example.com` | 学生 | 关注：计算机类、出国留学 |
| `prof@example.com` | 教授（张伟） | 计算机类、软件工程 |
| `prof2@example.com` | 副教授（李娜） | 电子信息类、通信工程 |
| `prof3@example.com` | 教授（王强） | 自动化类 |
| `prof4@example.com` | 讲师（陈静） | 数学类 |

> 说明：旧系统的问答数据为测试内容（`test`/`1111` 等）不可复用；参考数据则质量较高，已全部迁移。
> 演示问答为**原创撰写**（仅以公开考研/留学科普文章作为选题参考），未抓取或转载第三方内容。

## 容器化部署

`docker-compose.yml` 一次拉起 **PostgreSQL + 后端 + 前端（nginx）** 三个容器：

```bash
docker compose up -d --build
```

| 服务 | 容器 | 宿主机端口 | 说明 |
|---|---|---|---|
| 前端 | `edu-frontend` | **3000** | nginx 托管静态资源，并把 `/api` 反向代理到后端（同源，无 CORS） |
| 后端 | `edu-backend` | **8180** | Spring Boot，容器内 8080 |
| 数据库 | `edu-db` | **5433** | 避免与宿主机自带 PostgreSQL(5432) 冲突 |

访问 **http://localhost:3000**；接口文档 http://localhost:8180/swagger-ui.html。
首次启动会自动执行 Flyway 迁移并创建管理员 `admin / admin123`。

> 前端只需静态托管的场景：`pnpm build` 后将 `frontend/dist` 交给任意静态服务器，
> 并把 `/api` 反代到后端即可。`frontend/vite.config.ts` 支持 `VITE_API_TARGET` 覆盖开发代理目标。

停止 / 清理：

```bash
docker compose down          # 停止
docker compose down -v       # 停止并删除数据卷
```

## 主要接口

- 认证：`POST /api/auth/{send-code,register,login,refresh}`
- 用户：`GET/PUT /api/users/me`、`/api/users/me/interest-areas`
- 问答：`GET /api/public/questions`、`GET /api/questions/{id}`、`POST /api/questions[/paid]`、
  `POST /api/answers`、点赞/收藏 `POST|DELETE /api/{questions,answers}/{id}/like`
- 支持列表：`GET /api/questions/{mine,liked,recommend}`、`GET /api/answers/{collected,liked}`
- **评论**：`GET /api/comments?targetType={QUESTION|ANSWER}&targetId=`、`POST /api/comments`（支持 `parentId` 回复）、`DELETE /api/comments/{id}`
- 教授：`GET /api/professors`、`GET /api/professors/{id}`、`POST /api/professors/apply`
- **全站搜索**：`GET /api/public/search?keyword=&limit=`（聚合问答/教授/资讯/资料/指南）
- **通知**：`GET /api/notifications`、`/unread-count`、`/unread-by-type`、`POST /{id}/read`、`POST /read-all`
- **私信**：`GET/POST /api/conversations`、`GET /api/conversations/{id}/messages?beforeId=&afterId=`、
  `POST /api/conversations/{id}/messages`（`clientMsgId` 幂等，`type=IMAGE|FILE` 承载富媒体）、
  `POST /api/conversations/{id}/read`、`PATCH /api/conversations/{id}`（免打扰/置顶/隐藏）、
  `POST /api/conversations/{id}/messages/{messageId}/recall`、`GET /api/conversations/unread-count`
- **实时**：`GET /api/stream?token=`（单条 SSE，事件 `notification` / `message` / `read`；提交后推送）
- 后台：`/api/admin/{questions,users,professors,areas,taxonomy,study-guides,news}`（需 `*:manage` 权限）

## 设计说明

- **无对象关联的实体**：外键统一为 `Long` 列，读模型用批量查询装配（`QuestionAssembler` 等），
  避免懒加载与 N+1。
- **outbox**：`outbox_task` 表 + 定时轮询，邮件/短信失败按指数退避重试；未配置 SMTP 时降级为日志，
  本地无需任何外部服务即可跑通注册流程。
- **搜索**：`pg_trgm` GIN 索引加速 `ILIKE` 子串匹配，`similarity` 仅用于排序（对中文短词召回更稳）。
- **私信准入**：`user_interaction` 记录"教授回答学生问题"的关系，仅互动过的双方可建会话；
  会话/成员/消息三表建模，未读为成员列上冗余计数，消息按自增 `id` 游标分页，实时经统一 SSE 流推送。
- **支付**：`PaymentProvider` 抽象，默认 `MockPaymentProvider`；回调按 `(provider, tradeNo)` 幂等。
