-- =========================================================
-- 教授面对面 v2 · 全量 schema（PostgreSQL）
-- 约定：主键统一 bigint identity；时间 timestamptz；金额 numeric(12,2)
-- =========================================================

-- ---------- 文件 ----------
create table file_object (
    id            bigint generated always as identity primary key,
    storage_key   varchar(500) not null unique,
    original_name varchar(255) not null,
    content_type  varchar(128),
    size_bytes    bigint       not null,
    created_at    timestamptz  not null default now(),
    updated_at    timestamptz  not null default now()
);

-- ---------- 用户与身份 ----------
create table app_user (
    id             bigint generated always as identity primary key,
    username       varchar(64) unique,
    email          varchar(128) unique,
    phone          varchar(32) unique,
    password_hash  varchar(100) not null,
    role           varchar(20)  not null default 'USER',
    nickname       varchar(64),
    real_name      varchar(64),
    avatar_file_id bigint references file_object (id) on delete set null,
    gender         varchar(10),
    birthday       date,
    enabled        boolean      not null default true,
    created_at     timestamptz  not null default now(),
    updated_at     timestamptz  not null default now(),
    constraint ck_app_user_role check (role in ('USER', 'PROFESSOR', 'ADMIN'))
);
create index idx_app_user_role on app_user (role);

create table verification_code (
    id          bigint generated always as identity primary key,
    target      varchar(128) not null,
    channel     varchar(10)  not null,
    purpose     varchar(32)  not null,
    code        varchar(10)  not null,
    expires_at  timestamptz  not null,
    consumed_at timestamptz,
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now(),
    constraint ck_verification_channel check (channel in ('EMAIL', 'SMS'))
);
create index idx_verification_target on verification_code (target, purpose, expires_at);

create table login_log (
    id         bigint generated always as identity primary key,
    user_id    bigint      not null references app_user (id) on delete cascade,
    login_type varchar(20) not null,
    ip         varchar(64),
    device     varchar(255),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index idx_login_log_user on login_log (user_id);

-- ---------- 领域 / 元数据 ----------
create table consult_area (
    id         bigint generated always as identity primary key,
    name       varchar(128) not null,
    parent_id  bigint references consult_area (id) on delete cascade,
    sort_order int          not null default 0,
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now()
);
create index idx_consult_area_parent on consult_area (parent_id);

create table user_interest_area (
    id         bigint generated always as identity primary key,
    user_id    bigint      not null references app_user (id) on delete cascade,
    area_id    bigint      not null references consult_area (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_user_interest_area unique (user_id, area_id)
);

create table university (
    id         bigint generated always as identity primary key,
    name       varchar(200) not null,
    province   varchar(64),
    city       varchar(64),
    level      varchar(64),
    department varchar(128),
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now()
);
create index idx_university_name on university (name);

create table university_major (
    id         bigint generated always as identity primary key,
    code       varchar(32),
    name       varchar(200) not null,
    parent_id  bigint references university_major (id) on delete cascade,
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now()
);
create index idx_university_major_parent on university_major (parent_id);

create table research_direction (
    id         bigint generated always as identity primary key,
    name       varchar(200) not null,
    major_id   bigint references university_major (id) on delete set null,
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now()
);

create table job_rank (
    id         bigint generated always as identity primary key,
    name       varchar(64) not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table degree (
    id         bigint generated always as identity primary key,
    name       varchar(64) not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- ---------- 教授 ----------
create table professor_profile (
    user_id       bigint primary key references app_user (id) on delete cascade,
    job_rank_id   bigint references job_rank (id) on delete set null,
    introduction  text,
    consult_price numeric(12, 2) not null default 0,
    cv_file_id    bigint references file_object (id) on delete set null,
    approved      boolean        not null default false,
    created_at    timestamptz    not null default now(),
    updated_at    timestamptz    not null default now()
);

create table professor_area (
    id           bigint generated always as identity primary key,
    professor_id bigint      not null references app_user (id) on delete cascade,
    area_id      bigint      not null references consult_area (id) on delete cascade,
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),
    constraint uk_professor_area unique (professor_id, area_id)
);

create table professor_review (
    id           bigint generated always as identity primary key,
    professor_id bigint      not null references app_user (id) on delete cascade,
    user_id      bigint      not null references app_user (id) on delete cascade,
    rating       int         not null,
    content      varchar(1000),
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),
    constraint ck_professor_review_rating check (rating between 0 and 10)
);
create index idx_professor_review_prof on professor_review (professor_id);

create table education_record (
    id                 bigint generated always as identity primary key,
    professor_id       bigint       not null references app_user (id) on delete cascade,
    school_name        varchar(200) not null,
    major_name         varchar(200),
    degree_id          bigint references degree (id) on delete set null,
    start_date         date,
    end_date           date,
    full_time          boolean      not null default true,
    research_direction varchar(200),
    created_at         timestamptz  not null default now(),
    updated_at         timestamptz  not null default now()
);
create index idx_education_record_prof on education_record (professor_id);

-- ---------- 订单与支付 ----------
create table pay_order (
    id                bigint generated always as identity primary key,
    order_no          varchar(64) not null unique,
    user_id           bigint      not null references app_user (id),
    amount            numeric(12, 2) not null,
    status            varchar(16) not null default 'TO_PAY',
    provider          varchar(20),
    provider_trade_no varchar(128),
    subject           varchar(255),
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now(),
    paid_at           timestamptz,
    constraint ck_pay_order_status check (status in ('TO_PAY', 'PAID', 'CANCELLED', 'REFUNDED'))
);
create index idx_pay_order_user on pay_order (user_id);

create table payment_notification (
    id                bigint generated always as identity primary key,
    provider          varchar(20)  not null,
    provider_trade_no varchar(128) not null,
    order_no          varchar(64),
    raw_payload       text,
    created_at        timestamptz  not null default now(),
    updated_at        timestamptz  not null default now(),
    constraint uk_payment_notification unique (provider, provider_trade_no)
);

-- ---------- 问答 ----------
create table question (
    id          bigint generated always as identity primary key,
    author_id   bigint       not null references app_user (id),
    title       varchar(255) not null,
    description text,
    status      varchar(16)  not null default 'AUDITING',
    order_id    bigint references pay_order (id) on delete set null,
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now(),
    constraint ck_question_status check (status in ('AUDITING', 'NORMAL', 'FORBIDDEN', 'REJECTED', 'PRIVATE'))
);
create index idx_question_author on question (author_id);
create index idx_question_status on question (status);

create table question_area (
    id          bigint generated always as identity primary key,
    question_id bigint      not null references question (id) on delete cascade,
    area_id     bigint      not null references consult_area (id) on delete cascade,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    constraint uk_question_area unique (question_id, area_id)
);

create table question_professor (
    id           bigint generated always as identity primary key,
    question_id  bigint      not null references question (id) on delete cascade,
    professor_id bigint      not null references app_user (id) on delete cascade,
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),
    constraint uk_question_professor unique (question_id, professor_id)
);

create table question_image (
    id          bigint generated always as identity primary key,
    question_id bigint      not null references question (id) on delete cascade,
    file_id     bigint      not null references file_object (id) on delete cascade,
    sort_order  int         not null default 0,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    constraint uk_question_image unique (question_id, file_id)
);

create table answer (
    id           bigint generated always as identity primary key,
    question_id  bigint      not null references question (id) on delete cascade,
    professor_id bigint      not null references app_user (id),
    content      text        not null,
    status       varchar(16) not null default 'NORMAL',
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),
    constraint uk_answer_question_professor unique (question_id, professor_id),
    constraint ck_answer_status check (status in ('NORMAL', 'FORBIDDEN'))
);
create index idx_answer_question on answer (question_id);

create table question_like (
    id          bigint generated always as identity primary key,
    user_id     bigint      not null references app_user (id) on delete cascade,
    question_id bigint      not null references question (id) on delete cascade,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    constraint uk_question_like unique (user_id, question_id)
);

create table answer_like (
    id         bigint generated always as identity primary key,
    user_id    bigint      not null references app_user (id) on delete cascade,
    answer_id  bigint      not null references answer (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_answer_like unique (user_id, answer_id)
);

create table answer_collect (
    id         bigint generated always as identity primary key,
    user_id    bigint      not null references app_user (id) on delete cascade,
    answer_id  bigint      not null references answer (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_answer_collect unique (user_id, answer_id)
);

-- ---------- 学习指南 / 资料 / 资讯 ----------
create table study_guide (
    id         bigint generated always as identity primary key,
    name       varchar(255) not null,
    parent_id  bigint references study_guide (id) on delete cascade,
    important  boolean      not null default false,
    sort_order int          not null default 0,
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now()
);
create index idx_study_guide_parent on study_guide (parent_id);

create table guide_area (
    id         bigint generated always as identity primary key,
    guide_id   bigint      not null references study_guide (id) on delete cascade,
    area_id    bigint      not null references consult_area (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_guide_area unique (guide_id, area_id)
);

create table study_resource (
    id           bigint generated always as identity primary key,
    professor_id bigint       not null references app_user (id),
    name         varchar(255) not null,
    file_id      bigint       not null references file_object (id),
    remark       varchar(1000),
    created_at   timestamptz  not null default now(),
    updated_at   timestamptz  not null default now()
);
create index idx_study_resource_prof on study_resource (professor_id);

create table news (
    id            bigint generated always as identity primary key,
    title         varchar(255) not null,
    content       text,
    cover_file_id bigint references file_object (id) on delete set null,
    priority      int          not null default 0,
    index_show    boolean      not null default false,
    author_id     bigint references app_user (id) on delete set null,
    created_at    timestamptz  not null default now(),
    updated_at    timestamptz  not null default now()
);
create index idx_news_index_show on news (index_show, priority desc);

create table news_source (
    id         bigint generated always as identity primary key,
    news_id    bigint        not null references news (id) on delete cascade,
    title      varchar(255)  not null,
    url        varchar(1000) not null,
    created_at timestamptz   not null default now(),
    updated_at timestamptz   not null default now()
);

-- ---------- 消息 ----------
create table notification (
    id              bigint generated always as identity primary key,
    to_user_id      bigint      not null references app_user (id) on delete cascade,
    from_user_id    bigint,
    type            varchar(32) not null,
    resource_id     bigint,
    related_user_id bigint,
    is_read         boolean     not null default false,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);
create index idx_notification_to on notification (to_user_id, is_read);

create table direct_message (
    id           bigint generated always as identity primary key,
    from_user_id bigint        not null references app_user (id) on delete cascade,
    to_user_id   bigint        not null references app_user (id) on delete cascade,
    content      varchar(2000) not null,
    is_read      boolean       not null default false,
    created_at   timestamptz   not null default now(),
    updated_at   timestamptz   not null default now()
);
create index idx_direct_message_pair on direct_message (from_user_id, to_user_id, created_at desc);
create index idx_direct_message_to on direct_message (to_user_id, is_read);

-- ---------- 后台 RBAC ----------
create table app_role (
    id         bigint generated always as identity primary key,
    code       varchar(64) not null unique,
    name       varchar(128) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table permission (
    id         bigint generated always as identity primary key,
    code       varchar(64) not null unique,
    name       varchar(128) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table role_permission (
    id            bigint generated always as identity primary key,
    role_id       bigint      not null references app_role (id) on delete cascade,
    permission_id bigint      not null references permission (id) on delete cascade,
    created_at    timestamptz not null default now(),
    updated_at    timestamptz not null default now(),
    constraint uk_role_permission unique (role_id, permission_id)
);

create table admin_role (
    id         bigint generated always as identity primary key,
    user_id    bigint      not null references app_user (id) on delete cascade,
    role_id    bigint      not null references app_role (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_admin_role unique (user_id, role_id)
);

-- ---------- 异步 outbox ----------
create table outbox_task (
    id              bigint generated always as identity primary key,
    type            varchar(32)  not null,
    payload         jsonb        not null,
    status          varchar(16)  not null default 'PENDING',
    attempts        int          not null default 0,
    last_error      text,
    next_attempt_at timestamptz  not null default now(),
    created_at      timestamptz  not null default now(),
    updated_at      timestamptz  not null default now(),
    completed_at    timestamptz
);
create index idx_outbox_status on outbox_task (status, next_attempt_at);

-- ---------- 全文检索（pg_trgm）----------
create extension if not exists pg_trgm;
create index idx_question_title_trgm on question using gin (title gin_trgm_ops);
create index idx_question_desc_trgm on question using gin (description gin_trgm_ops);
create index idx_app_user_real_name_trgm on app_user using gin (real_name gin_trgm_ops);
