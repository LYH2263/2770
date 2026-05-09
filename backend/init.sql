SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = 'utf8mb4_unicode_ci';

-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '密码',
    nickname VARCHAR(50) CHARACTER SET utf8mb4 COMMENT '昵称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建学生表
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_no VARCHAR(20) CHARACTER SET utf8mb4 NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '姓名',
    age INT COMMENT '年龄',
    gender VARCHAR(10) CHARACTER SET utf8mb4 COMMENT '性别',
    major VARCHAR(100) CHARACTER SET utf8mb4 COMMENT '专业',
    phone VARCHAR(20) CHARACTER SET utf8mb4 COMMENT '手机号',
    email VARCHAR(100) CHARACTER SET utf8mb4 COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_no VARCHAR(20) CHARACTER SET utf8mb4 NOT NULL UNIQUE COMMENT '课程编号',
    course_name VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '课程名称',
    credit DOUBLE NOT NULL COMMENT '学分',
    teacher VARCHAR(50) CHARACTER SET utf8mb4 COMMENT '授课教师',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 创建成绩表
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    usual_score DOUBLE DEFAULT 0 COMMENT '平时成绩',
    exam_score DOUBLE DEFAULT 0 COMMENT '期末成绩',
    total_score DOUBLE DEFAULT 0 COMMENT '总评成绩',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- 插入演示用户数据
INSERT IGNORE INTO users (username, password, nickname) VALUES ('admin', '123456', '管理员');

-- 插入演示学生数据
INSERT IGNORE INTO students (student_no, name, age, gender, major, phone, email) VALUES
('2024001', '张三', 20, '男', '计算机科学与技术', '13800138001', 'zhangsan@example.com'),
('2024002', '李四', 21, '女', '软件工程', '13800138002', 'lisi@example.com'),
('2024003', '王五', 19, '男', '数据科学与大数据技术', '13800138003', 'wangwu@example.com'),
('2024004', '赵六', 22, '女', '人工智能', '13800138004', 'zhaoliu@example.com'),
('2024005', '孙七', 20, '男', '网络工程', '13800138005', 'sunqi@example.com'),
('2024006', '周八', 21, '女', '信息安全', '13800138006', 'zhouba@example.com'),
('2024007', '吴九', 19, '男', '物联网工程', '13800138007', 'wujiu@example.com'),
('2024008', '郑十', 22, '女', '计算机科学与技术', '13800138008', 'zhengshi@example.com');

-- 插入演示课程数据
INSERT IGNORE INTO courses (course_no, course_name, credit, teacher) VALUES
('CS101', '高等数学', 4.0, '李教授'),
('CS102', '线性代数', 3.0, '王教授'),
('CS103', '大学物理', 3.0, '张教授'),
('CS104', '程序设计基础', 4.0, '刘教授'),
('CS105', '数据结构', 3.5, '陈教授'),
('CS106', '计算机网络', 3.0, '赵教授');

-- 插入演示成绩数据（平时成绩30% + 期末成绩70% = 总评）
INSERT IGNORE INTO scores (student_id, course_id, usual_score, exam_score, total_score) VALUES
(1, 1, 85, 88, 87.1),
(1, 2, 90, 82, 84.4),
(1, 3, 78, 85, 82.9),
(1, 4, 92, 95, 94.1),
(1, 5, 88, 90, 89.4),
(1, 6, 75, 80, 78.5),
(2, 1, 82, 78, 79.2),
(2, 2, 88, 90, 89.4),
(2, 3, 76, 72, 73.2),
(2, 4, 95, 92, 92.9),
(2, 5, 80, 85, 83.5),
(2, 6, 90, 88, 88.6),
(3, 1, 70, 65, 66.5),
(3, 2, 75, 68, 70.1),
(3, 3, 80, 82, 81.4),
(3, 4, 85, 88, 87.1),
(3, 5, 72, 70, 70.6),
(3, 6, 68, 55, 58.9),
(4, 1, 95, 92, 92.9),
(4, 2, 92, 88, 89.2),
(4, 3, 88, 90, 89.4),
(4, 4, 90, 85, 86.5),
(4, 5, 94, 96, 95.4),
(4, 6, 86, 82, 83.2),
(5, 1, 60, 55, 56.5),
(5, 2, 65, 58, 60.1),
(5, 3, 70, 62, 64.4),
(5, 4, 78, 72, 73.8),
(5, 5, 72, 68, 69.2),
(5, 6, 55, 45, 48.0),
(6, 1, 88, 85, 85.9),
(6, 2, 82, 78, 79.2),
(6, 3, 90, 92, 91.4),
(6, 4, 85, 80, 81.5),
(6, 5, 78, 82, 80.8),
(6, 6, 92, 90, 90.6),
(7, 1, 75, 70, 71.5),
(7, 2, 80, 76, 77.2),
(7, 3, 68, 62, 63.8),
(7, 4, 82, 85, 84.1),
(7, 5, 70, 65, 66.5),
(7, 6, 78, 72, 73.8),
(8, 1, 90, 88, 88.6),
(8, 2, 85, 82, 82.9),
(8, 3, 92, 90, 90.6),
(8, 4, 88, 85, 85.9),
(8, 5, 80, 78, 78.6),
(8, 6, 95, 93, 93.6);
