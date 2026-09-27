-- =========================================================
-- V5 · 演示学生的关注领域（用于验证推荐）
-- =========================================================
insert into user_interest_area (user_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'student' and a.name = '计算机类' limit 1;

insert into user_interest_area (user_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'student' and a.name = '出国留学' limit 1;
