-- =========================================================
-- V6 · 评论（问题 / 回答下的讨论与回复）
-- =========================================================
create table comment (
    id          bigint generated always as identity primary key,
    target_type varchar(16)  not null,
    target_id   bigint       not null,
    user_id     bigint       not null references app_user (id) on delete cascade,
    parent_id   bigint references comment (id) on delete cascade,
    content     varchar(1000) not null,
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now(),
    constraint ck_comment_target check (target_type in ('QUESTION', 'ANSWER'))
);
create index idx_comment_target on comment (target_type, target_id, created_at);
create index idx_comment_user on comment (user_id);
