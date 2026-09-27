-- =========================================================
-- V4 · 初期演示内容（原创内容，非抓取）
--   · 演示账号：student / prof / prof2 / prof3 / prof4（密码均为 password123）
--   · 教授资料、教育经历、可答领域
--   · 10 条问答（含教授回答）
--   · 3 条资讯、学习指南树
-- 说明：管理账号由应用启动时自动创建（admin / admin123）。
-- =========================================================

-- ---------- 演示用户 ----------
insert into app_user (username, email, password_hash, role, nickname, real_name, enabled) values
    ('student', 'student@example.com', '$2a$10$HZbIJHa0wGVh6s2V6DepAOKrG72aGc8xe0eAk0WkOdkr1oWlAE54u', 'USER', '小明', '李明', true),
    ('prof', 'prof@example.com', '$2a$10$HZbIJHa0wGVh6s2V6DepAOKrG72aGc8xe0eAk0WkOdkr1oWlAE54u', 'PROFESSOR', '张伟教授', '张伟', true),
    ('prof2', 'prof2@example.com', '$2a$10$HZbIJHa0wGVh6s2V6DepAOKrG72aGc8xe0eAk0WkOdkr1oWlAE54u', 'PROFESSOR', '李娜老师', '李娜', true),
    ('prof3', 'prof3@example.com', '$2a$10$HZbIJHa0wGVh6s2V6DepAOKrG72aGc8xe0eAk0WkOdkr1oWlAE54u', 'PROFESSOR', '王强老师', '王强', true),
    ('prof4', 'prof4@example.com', '$2a$10$HZbIJHa0wGVh6s2V6DepAOKrG72aGc8xe0eAk0WkOdkr1oWlAE54u', 'PROFESSOR', '陈静老师', '陈静', true);

-- ---------- 教授资料 ----------
insert into professor_profile (user_id, job_rank_id, introduction, consult_price, approved)
select u.id, (select id from job_rank where name = '教授' limit 1),
       '北京大学计算机学院教授，研究方向为机器学习与推荐系统。长期指导本科毕业设计与考研复试，擅长帮助同学梳理研究方向与择校思路。',
       60, true
from app_user u where u.username = 'prof';

insert into professor_profile (user_id, job_rank_id, introduction, consult_price, approved)
select u.id, (select id from job_rank where name = '副教授' limit 1),
       '电子科技大学信息与通信工程学院副教授，从事信号处理与无线通信研究。熟悉电子信息类考研科目与院校选择。',
       45, true
from app_user u where u.username = 'prof2';

insert into professor_profile (user_id, job_rank_id, introduction, consult_price, approved)
select u.id, (select id from job_rank where name = '教授' limit 1),
       '哈尔滨工业大学控制科学与工程方向教授，主要研究机器人感知与控制。欢迎对自动化、机器人方向感兴趣的同学咨询。',
       55, true
from app_user u where u.username = 'prof3';

insert into professor_profile (user_id, job_rank_id, introduction, consult_price, approved)
select u.id, (select id from job_rank where name = '讲师' limit 1),
       '浙江大学数学科学学院讲师，研究方向为最优化与统计学习。可为跨考人工智能方向的同学解答数学基础相关问题。',
       35, true
from app_user u where u.username = 'prof4';

-- ---------- 教育经历 ----------
insert into education_record (professor_id, school_name, major_name, degree_id, start_date, end_date, full_time, research_direction)
select u.id, '北京大学', '计算机科学与技术', (select id from degree where name = '博士' limit 1),
       '2010-09-01', '2015-06-30', true, '人工智能'
from app_user u where u.username = 'prof';

insert into education_record (professor_id, school_name, major_name, degree_id, start_date, end_date, full_time, research_direction)
select u.id, '电子科技大学', '信息与通信工程', (select id from degree where name = '博士' limit 1),
       '2011-09-01', '2016-06-30', true, '信号处理'
from app_user u where u.username = 'prof2';

insert into education_record (professor_id, school_name, major_name, degree_id, start_date, end_date, full_time, research_direction)
select u.id, '哈尔滨工业大学', '控制科学与工程', (select id from degree where name = '博士' limit 1),
       '2009-09-01', '2014-06-30', true, '机器人控制'
from app_user u where u.username = 'prof3';

insert into education_record (professor_id, school_name, major_name, degree_id, start_date, end_date, full_time, research_direction)
select u.id, '浙江大学', '数学与应用数学', (select id from degree where name = '博士' limit 1),
       '2013-09-01', '2018-06-30', true, '最优化'
from app_user u where u.username = 'prof4';

-- ---------- 可答领域 ----------
insert into professor_area (professor_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'prof' and a.name in ('计算机类') limit 1;
insert into professor_area (professor_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'prof' and a.name in ('软件工程') limit 1;

insert into professor_area (professor_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'prof2' and a.name in ('电子信息类') limit 1;
insert into professor_area (professor_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'prof2' and a.name in ('通信工程') limit 1;

insert into professor_area (professor_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'prof3' and a.name in ('自动化类') limit 1;

insert into professor_area (professor_id, area_id)
select u.id, a.id from app_user u, consult_area a
where u.username = 'prof4' and a.name in ('数学类') limit 1;

-- ---------- 问答 ----------
insert into question (author_id, title, description, status)
select u.id, '考研计算机方向如何选择？',
       '本科软件工程，想报考计算机相关专业，学硕和专硕、以及人工智能等热门方向应该怎么选？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '408 统考和自命题该怎么取舍？',
       '目标院校有的考 408，有的自命题，复习策略差别很大，该如何决策？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '人工智能方向需要哪些数学基础？',
       '想跨考人工智能方向，数学需要补到什么程度？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '电子信息考研和计算机考研有什么区别？',
       '两个方向在考试科目、研究方向与就业上有什么不同？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '自动化专业跨考人工智能难度大吗？',
       '自动化本科，编程基础一般，跨考人工智能是否现实？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '出国留学和国内考研应该怎么选？',
       '目前大二，家里支持留学，但也在准备考研，如何取舍？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '保研失败后如何调整考研计划？',
       '保研失利，距离初试时间不多，如何快速进入状态？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '复试联系导师需要注意什么？',
       '初试成绩出来后联系导师，邮件和沟通上有哪些注意点？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '双非本科考研如何科学择校？',
       '出身双非，担心被歧视，择校上有什么建议？', 'NORMAL'
from app_user u where u.username = 'student';
insert into question (author_id, title, description, status)
select u.id, '大三下才开始准备考研来得及吗？',
       '大三下才决定考研，时间是否够用，如何安排复习节奏？', 'NORMAL'
from app_user u where u.username = 'student';

-- 问题领域
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '考研计算机方向如何选择？' and a.name = '计算机类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '408 统考和自命题该怎么取舍？' and a.name = '计算机类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '人工智能方向需要哪些数学基础？' and a.name = '数学类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '电子信息考研和计算机考研有什么区别？' and a.name = '电子信息类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '自动化专业跨考人工智能难度大吗？' and a.name = '自动化类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '出国留学和国内考研应该怎么选？' and a.name = '出国留学' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '保研失败后如何调整考研计划？' and a.name = '计算机类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '复试联系导师需要注意什么？' and a.name = '电子信息类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '双非本科考研如何科学择校？' and a.name = '计算机类' limit 1;
insert into question_area (question_id, area_id)
select q.id, a.id from question q, consult_area a
where q.title = '大三下才开始准备考研来得及吗？' and a.name = '自动化类' limit 1;

-- 定向教授
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '考研计算机方向如何选择？' and u.username = 'prof';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '408 统考和自命题该怎么取舍？' and u.username = 'prof';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '人工智能方向需要哪些数学基础？' and u.username = 'prof4';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '电子信息考研和计算机考研有什么区别？' and u.username = 'prof2';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '自动化专业跨考人工智能难度大吗？' and u.username = 'prof3';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '出国留学和国内考研应该怎么选？' and u.username = 'prof';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '保研失败后如何调整考研计划？' and u.username = 'prof';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '复试联系导师需要注意什么？' and u.username = 'prof2';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '双非本科考研如何科学择校？' and u.username = 'prof';
insert into question_professor (question_id, professor_id)
select q.id, u.id from question q, app_user u where q.title = '大三下才开始准备考研来得及吗？' and u.username = 'prof3';

-- 教授回答
insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '先分清两条主线：想做科研、考虑读博，优先学硕（计算机科学与技术等）；想尽快就业，优先专硕（电子信息大类下的计算机技术、软件工程、人工智能等）。择校时重点关注三点：真实统考名额（招生数减去推免数）、近三年录取均分与报录比、专业课科目是否稳定。方向选择上，人工智能竞争最激烈，建议结合自己数学与编程基础理性评估，不必盲目追热门。',
       'NORMAL'
from question q, app_user u where q.title = '考研计算机方向如何选择？' and u.username = 'prof';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '408 覆盖数据结构、计算机组成原理、操作系统、计算机网络，范围固定、资料齐全、可报考院校逐年增多；自命题范围小但变数大，招生简章调整时会很被动。如果不确定最终院校，建议按 408 准备，进可攻退可守。若已有明确目标且其自命题科目稳定、真题易得，再考虑自命题。',
       'NORMAL'
from question q, app_user u where q.title = '408 统考和自命题该怎么取舍？' and u.username = 'prof';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '核心是线性代数、概率论与数理统计、微积分，以及在此基础上的一门优化入门。跨考同学建议先把线代和概率论吃透，再补最优化与机器学习中的数学推导。不必一开始就追求高深，能看懂梯度、矩阵求导、常见分布即可，后续在项目中逐步加深。',
       'NORMAL'
from question q, app_user u where q.title = '人工智能方向需要哪些数学基础？' and u.username = 'prof4';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '考试科目上，计算机多考 408 或数据结构相关自命题，电子信息常考信号与系统、通信原理等；研究方向上，计算机偏软件与算法，电子信息偏信号、通信与硬件系统。就业上两者都有互联网岗位，电子信息在中兴、华为等通信与硬件企业更对口。建议按本科基础和兴趣选择，跨考计算机需自行补齐编程与算法。',
       'NORMAL'
from question q, app_user u where q.title = '电子信息考研和计算机考研有什么区别？' and u.username = 'prof2';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '不算特别大，但需要补两块：一是编程与算法（C/Python + 数据结构 + 刷题），二是机器学习基础。自动化在控制、信号与数学上有优势，转向机器人、智能控制、边缘智能等交叉方向其实是加分项。建议至少提前半年系统准备，用一个小项目把知识串起来。',
       'NORMAL'
from question q, app_user u where q.title = '自动化专业跨考人工智能难度大吗？' and u.username = 'prof3';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '取决于目标与家庭规划。留学更看重语言成绩、科研/实习背景与经费，适合希望接受国际化培养、目标海外就业或读博的同学；国内考研更依赖统考成绩，路径相对单一但成本可控。可以两条线并行到语言考试与初试前，但要尽早根据成绩与offer情况收敛，避免两边都准备不透。',
       'NORMAL'
from question q, app_user u where q.title = '出国留学和国内考研应该怎么选？' and u.username = 'prof';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '保研失利后先稳住心态，把目标从“最优”调整为“可行”。当务之急是确定 1-2 所匹配院校，明确考试科目，然后立刻进入数学与专业课的强化。复盘保研材料中的项目经历，转化为复试可讲的科研亮点，这往往比重新规划更有价值。',
       'NORMAL'
from question q, app_user u where q.title = '保研失败后如何调整考研计划？' and u.username = 'prof';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '邮件要简短、有针对性：一页内说清本科背景、初试成绩、感兴趣的方向和读过的相关工作，附上简历。避免群发模板，最好读过对方近两年的论文再写。得到回复后保持礼貌跟进，不要频繁催促；复试前再确认一次即可。',
       'NORMAL'
from question q, app_user u where q.title = '复试联系导师需要注意什么？' and u.username = 'prof2';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '不必自我设限。多数院校复试按初试成绩和复试表现打分，规范程度逐年提高。择校时优先选择保护一志愿、不压分、统考名额充足的院校，用“冲一档、稳一档、保一档”的梯度组合。把初试分数做扎实，是双非同学最有效的竞争力。',
       'NORMAL'
from question q, app_user u where q.title = '双非本科考研如何科学择校？' and u.username = 'prof';

insert into answer (question_id, professor_id, content, status)
select q.id, u.id,
       '来得及，但要立刻开始并提高效率。建议 3-4 个月打基础（数学 + 英语 + 专业课框架），暑期强化刷题，秋季做真题与模拟。关键是每天固定投入并复盘错题，避免“学了很多、没练多少”。专业课尽早确定科目，按考纲推进。',
       'NORMAL'
from question q, app_user u where q.title = '大三下才开始准备考研来得及吗？' and u.username = 'prof3';

-- ---------- 资讯 ----------
insert into news (title, content, priority, index_show) values
('欢迎使用「教授面对面」', '这里是学生与教授交流的平台。你可以浏览并提问考研择校、专业方向与学习规划相关问题，查看教授资料与学习资料。演示账号：student@example.com / password123。', 100, true),
('考研时间线提醒', '通常建议：3-6 月打基础，7-9 月强化刷题，10 月报名确认，11-12 月冲刺模考。请以目标院校最新招生简章为准。', 60, true),
('如何用好付费咨询', '在提问时可勾选「付费咨询」并选择一位教授，生成订单完成支付后，教授将收到定向提醒。建议问题描述尽量具体，便于获得有针对性的解答。', 40, true);

-- ---------- 学习指南 ----------
insert into study_guide (name, parent_id, important, sort_order) values ('考研准备', null, true, 1);
insert into study_guide (name, parent_id, important, sort_order)
select '信息收集与择校', id, true, 1 from study_guide where name = '考研准备' and parent_id is null;
insert into study_guide (name, parent_id, important, sort_order)
select '初试复习', id, true, 2 from study_guide where name = '考研准备' and parent_id is null;
insert into study_guide (name, parent_id, important, sort_order)
select '复试与联系导师', id, false, 3 from study_guide where name = '考研准备' and parent_id is null;
insert into study_guide (name, parent_id, important, sort_order)
select '数学', id, true, 1 from study_guide where name = '初试复习' and parent_id is not null;
insert into study_guide (name, parent_id, important, sort_order)
select '专业课', id, true, 2 from study_guide where name = '初试复习' and parent_id is not null;
insert into study_guide (name, parent_id, important, sort_order)
select '英语与政治', id, false, 3 from study_guide where name = '初试复习' and parent_id is not null;

insert into study_guide (name, parent_id, important, sort_order) values ('出国留学', null, false, 2);
insert into study_guide (name, parent_id, important, sort_order)
select '语言考试', id, false, 1 from study_guide where name = '出国留学' and parent_id is null;
insert into study_guide (name, parent_id, important, sort_order)
select '选校与文书', id, false, 2 from study_guide where name = '出国留学' and parent_id is null;

-- 指南关联领域
insert into guide_area (guide_id, area_id)
select g.id, a.id from study_guide g, consult_area a
where g.name = '考研准备' and a.name = '计算机类' limit 1;
insert into guide_area (guide_id, area_id)
select g.id, a.id from study_guide g, consult_area a
where g.name = '出国留学' and a.name = '出国留学' limit 1;
