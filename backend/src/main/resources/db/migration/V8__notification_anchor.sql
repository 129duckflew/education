-- =========================================================
-- V8 · 通知深链锚点：让通知可直达问题、回答或具体评论
--   anchor_type  = ANSWER | QUESTION_COMMENT | ANSWER_COMMENT
--   anchor_id    = 对应 回答id / 评论id
--   anchor_ref_id= ANSWER_COMMENT 时所属回答 id（用于展开该回答的评论区）
-- resource_id 统一为问题 id，前端据此打开 /questions/{resourceId}
-- =========================================================
alter table notification add column anchor_type varchar(24);
alter table notification add column anchor_id bigint;
alter table notification add column anchor_ref_id bigint;
