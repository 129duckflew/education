-- =========================================================
-- V7 · 私信：会话 / 成员 / 消息 + 互动关系（私信准入）
-- 旧版 direct_message 弃用，直接删除
-- =========================================================
drop table if exists direct_message;

-- 会话（1:1 与群聊统一建模，当前仅开放 SINGLE，GROUP 字段预留）
create table conversation (
    id              bigint generated always as identity primary key,
    type            varchar(16) not null default 'SINGLE',
    pair_key        varchar(64),
    title           varchar(64),
    owner_id        bigint references app_user (id) on delete set null,
    last_message_id bigint,
    last_message_at timestamptz,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now(),
    constraint ck_conversation_type check (type in ('SINGLE', 'GROUP'))
);
create unique index uk_conversation_pair on conversation (pair_key);
create index idx_conversation_last on conversation (last_message_at desc);

-- 会话成员：已读游标 / 未读计数 / 免打扰 / 置顶 / 仅本人删除
create table conversation_member (
    id                   bigint generated always as identity primary key,
    conversation_id      bigint      not null references conversation (id) on delete cascade,
    user_id              bigint      not null references app_user (id) on delete cascade,
    role                 varchar(16) not null default 'MEMBER',
    last_read_message_id bigint      not null default 0,
    unread_count         int         not null default 0,
    muted                boolean     not null default false,
    pinned               boolean     not null default false,
    hidden               boolean     not null default false,
    created_at           timestamptz not null default now(),
    updated_at           timestamptz not null default now(),
    constraint uk_conversation_member unique (conversation_id, user_id)
);
create index idx_member_user on conversation_member (user_id, pinned desc, conversation_id);

-- 消息：服务端自增 id 即权威顺序；client_msg_id 幂等；file_id 承载富媒体
create table message (
    id              bigint generated always as identity primary key,
    conversation_id bigint       not null references conversation (id) on delete cascade,
    sender_id       bigint       not null references app_user (id) on delete cascade,
    type            varchar(16)  not null default 'TEXT',
    content         varchar(4000),
    file_id         bigint       references file_object (id) on delete set null,
    reply_to_id     bigint,
    client_msg_id   varchar(64),
    status          varchar(16)  not null default 'NORMAL',
    created_at      timestamptz  not null default now(),
    updated_at      timestamptz  not null default now(),
    constraint ck_message_type check (type in ('TEXT', 'IMAGE', 'FILE', 'SYSTEM')),
    constraint ck_message_status check (status in ('NORMAL', 'RECALLED'))
);
create index idx_message_conv on message (conversation_id, id desc);
create unique index uk_message_client on message (sender_id, client_msg_id)
    where client_msg_id is not null;

-- 互动关系：仅由「教授回答学生问题」建立，用于私信准入（恒 user_a_id < user_b_id）
create table user_interaction (
    id         bigint generated always as identity primary key,
    user_a_id  bigint      not null references app_user (id) on delete cascade,
    user_b_id  bigint      not null references app_user (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint ck_interaction_order check (user_a_id < user_b_id),
    constraint uk_interaction_pair unique (user_a_id, user_b_id)
);
create index idx_interaction_a on user_interaction (user_a_id);
create index idx_interaction_b on user_interaction (user_b_id);

-- 从既有回答回填互动关系（让已有问答一键解锁私信）
insert into user_interaction (user_a_id, user_b_id)
select distinct least(q.author_id, a.professor_id), greatest(q.author_id, a.professor_id)
from answer a
join question q on q.id = a.question_id
where q.author_id <> a.professor_id
on conflict (user_a_id, user_b_id) do nothing;
