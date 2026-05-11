-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建学生表
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    age INT COMMENT '年龄',
    gender VARCHAR(10) COMMENT '性别',
    major VARCHAR(100) COMMENT '专业',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_no VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credit INT COMMENT '学分',
    teacher VARCHAR(50) COMMENT '授课教师',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 创建成绩表
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    daily_score DECIMAL(5,2) COMMENT '平时成绩',
    final_score DECIMAL(5,2) COMMENT '期末成绩',
    total_score DECIMAL(5,2) COMMENT '总评成绩',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- 插入演示数据 - 用户
INSERT INTO users (username, password, nickname) VALUES 
('admin', '123456', '系统管理员')
ON DUPLICATE KEY UPDATE nickname = nickname;

-- 插入演示数据 - 学生
INSERT INTO students (student_no, name, age, gender, major, phone, email) VALUES 
('2024001', '张三', 20, '男', '计算机科学与技术', '13800138001', 'zhangsan@example.com'),
('2024002', '李四', 21, '女', '计算机科学与技术', '13800138002', 'lisi@example.com'),
('2024003', '王五', 19, '男', '软件工程', '13800138003', 'wangwu@example.com'),
('2024004', '赵六', 20, '女', '软件工程', '13800138004', 'zhaoliu@example.com'),
('2024005', '孙七', 22, '男', '电子信息工程', '13800138005', 'sunqi@example.com'),
('2024006', '周八', 21, '女', '电子信息工程', '13800138006', 'zhouba@example.com'),
('2024007', '吴九', 20, '男', '数据科学与大数据技术', '13800138007', 'wujiu@example.com'),
('2024008', '郑十', 19, '女', '数据科学与大数据技术', '13800138008', 'zhengshi@example.com'),
('2024009', '钱十一', 21, '男', '人工智能', '13800138009', 'qianshiyi@example.com'),
('2024010', '陈十二', 20, '女', '人工智能', '13800138010', 'chenshier@example.com'),
('2024011', '刘十三', 22, '男', '计算机科学与技术', '13800138011', 'liushisan@example.com'),
('2024012', '黄十四', 19, '女', '软件工程', '13800138012', 'huangshisi@example.com')
ON DUPLICATE KEY UPDATE name = name;

-- 插入演示数据 - 课程
INSERT INTO courses (course_no, course_name, credit, teacher) VALUES 
('CS101', '高等数学', 4, '张教授'),
('CS102', '大学英语', 3, '李教授'),
('CS103', '程序设计基础', 4, '王教授'),
('CS104', '数据结构', 3, '赵教授'),
('CS105', '操作系统', 3, '孙教授'),
('CS106', '计算机网络', 3, '周教授'),
('CS107', '数据库原理', 3, '吴教授'),
('CS108', '机器学习', 4, '郑教授')
ON DUPLICATE KEY UPDATE course_name = course_name;

-- 插入演示数据 - 成绩
INSERT INTO scores (student_id, course_id, daily_score, final_score, total_score) VALUES 
-- 张三的成绩
(1, 1, 85.5, 88.0, 87.25),
(1, 2, 90.0, 92.0, 91.4),
(1, 3, 88.0, 85.0, 85.9),
(1, 4, 92.0, 95.0, 94.1),
(1, 5, 78.0, 82.0, 80.8),
-- 李四的成绩
(2, 1, 95.0, 98.0, 97.1),
(2, 2, 88.0, 90.0, 89.4),
(2, 3, 92.0, 88.0, 89.2),
(2, 4, 85.0, 80.0, 81.5),
(2, 5, 90.0, 94.0, 92.8),
-- 王五的成绩
(3, 1, 75.0, 70.0, 71.5),
(3, 2, 82.0, 78.0, 79.2),
(3, 3, 88.0, 85.0, 85.9),
(3, 6, 90.0, 88.0, 88.6),
(3, 7, 85.0, 82.0, 82.9),
-- 赵六的成绩
(4, 1, 88.0, 90.0, 89.4),
(4, 2, 95.0, 92.0, 92.9),
(4, 3, 78.0, 75.0, 75.9),
(4, 6, 82.0, 85.0, 84.1),
(4, 7, 90.0, 88.0, 88.6),
-- 孙七的成绩
(5, 1, 65.0, 58.0, 60.1),
(5, 2, 72.0, 68.0, 69.2),
(5, 3, 55.0, 60.0, 58.5),
(5, 4, 70.0, 75.0, 73.5),
(5, 5, 68.0, 70.0, 69.4),
-- 周八的成绩
(6, 1, 92.0, 95.0, 94.1),
(6, 2, 88.0, 90.0, 89.4),
(6, 3, 85.0, 82.0, 82.9),
(6, 4, 90.0, 88.0, 88.6),
(6, 5, 85.0, 80.0, 81.5),
-- 吴九的成绩
(7, 1, 78.0, 82.0, 80.8),
(7, 2, 85.0, 80.0, 81.5),
(7, 6, 92.0, 95.0, 94.1),
(7, 7, 88.0, 90.0, 89.4),
(7, 8, 90.0, 88.0, 88.6),
-- 郑十的成绩
(8, 1, 85.0, 88.0, 87.1),
(8, 2, 90.0, 92.0, 91.4),
(8, 6, 78.0, 75.0, 75.9),
(8, 7, 82.0, 85.0, 84.1),
(8, 8, 95.0, 98.0, 97.1),
-- 钱十一的成绩
(9, 1, 58.0, 55.0, 55.9),
(9, 2, 65.0, 60.0, 61.5),
(9, 3, 70.0, 68.0, 68.6),
(9, 4, 62.0, 58.0, 59.2),
(9, 8, 75.0, 72.0, 72.9),
-- 陈十二的成绩
(10, 1, 90.0, 92.0, 91.4),
(10, 2, 88.0, 85.0, 85.9),
(10, 3, 95.0, 98.0, 97.1),
(10, 4, 85.0, 88.0, 87.1),
(10, 8, 92.0, 95.0, 94.1)
ON DUPLICATE KEY UPDATE total_score = VALUES(total_score);
