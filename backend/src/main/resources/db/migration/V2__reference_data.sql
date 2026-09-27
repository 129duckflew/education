-- =========================================================
-- 参考数据 / 字典 / RBAC 种子
-- =========================================================

insert into job_rank (name) values ('教授'), ('副教授'), ('讲师'), ('助教');

insert into degree (name) values ('学士'), ('硕士'), ('博士');

insert into consult_area (name, parent_id, sort_order) values
    ('工学', null, 1),
    ('理学', null, 2),
    ('经济学', null, 3),
    ('管理学', null, 4),
    ('文学', null, 5),
    ('法学', null, 6),
    ('医学', null, 7),
    ('教育学', null, 8);

insert into consult_area (name, parent_id, sort_order)
select '计算机科学与技术', id, 1 from consult_area where name = '工学';
insert into consult_area (name, parent_id, sort_order)
select '软件工程', id, 2 from consult_area where name = '工学';
insert into consult_area (name, parent_id, sort_order)
select '电子信息工程', id, 3 from consult_area where name = '工学';
insert into consult_area (name, parent_id, sort_order)
select '数学', id, 1 from consult_area where name = '理学';

-- 后台 RBAC
insert into app_role (code, name) values
    ('SUPER_ADMIN', '超级管理员'),
    ('OPERATOR', '运营');

insert into permission (code, name) values
    ('dashboard:view', '查看仪表盘'),
    ('user:manage', '用户管理'),
    ('professor:manage', '教授管理'),
    ('question:manage', '问题审核'),
    ('answer:manage', '回答管理'),
    ('news:manage', '资讯管理'),
    ('taxonomy:manage', '领域/学校/专业管理'),
    ('guide:manage', '学习指南管理'),
    ('role:manage', '角色权限管理'),
    ('file:manage', '文件管理');

insert into role_permission (role_id, permission_id)
select r.id, p.id
from app_role r
cross join permission p
where r.code = 'SUPER_ADMIN';

insert into role_permission (role_id, permission_id)
select r.id, p.id
from app_role r
join permission p on p.code in ('dashboard:view', 'user:manage', 'professor:manage', 'question:manage', 'answer:manage', 'news:manage')
where r.code = 'OPERATOR';
