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

## 容器化（可选）

`docker-compose.yml` 提供 PostgreSQL + 后端的完整栈；若已使用宿主机 PG，可只构建后端镜像：

```bash
docker compose up -d --build backend
```

## 主要接口

- 认证：`POST /api/auth/{send-code,register,login,refresh}`
- 用户：`GET/PUT /api/users/me`、`/api/users/me/interest-areas`
- 问答：`GET /api/public/questions`、`GET /api/questions/{id}`、`POST /api/questions[/paid]`、
  `POST /api/answers`、点赞/收藏 `POST|DELETE /api/{questions,answers}/{id}/like`
- 教授：`GET /api/professors`、`GET /api/professors/{id}`、`POST /api/professors/apply`
- 搜索：`GET /api/public/search/{questions,professors}?keyword=`
- 后台：`/api/admin/{questions,users,professors,areas,taxonomy,study-guides,news}`（需 `*:manage` 权限）

## 设计说明

- **无对象关联的实体**：外键统一为 `Long` 列，读模型用批量查询装配（`QuestionAssembler` 等），
  避免懒加载与 N+1。
- **outbox**：`outbox_task` 表 + 定时轮询，邮件/短信失败按指数退避重试；未配置 SMTP 时降级为日志，
  本地无需任何外部服务即可跑通注册流程。
- **搜索**：`pg_trgm` GIN 索引 + `%` 相似度算子，按标题相似度排序。
- **支付**：`PaymentProvider` 抽象，默认 `MockPaymentProvider`；回调按 `(provider, tradeNo)` 幂等。
