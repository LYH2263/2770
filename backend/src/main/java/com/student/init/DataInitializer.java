package com.student.init;

import com.student.entity.Course;
import com.student.entity.Score;
import com.student.entity.Student;
import com.student.entity.User;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.mapper.StudentMapper;
import com.student.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public void run(String... args) {
        // 初始化用户数据
        if (userMapper.selectCount(null) == 0) {
            logger.info("开始初始化用户数据...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setNickname("管理员");
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userMapper.insert(admin);
            
            logger.info("用户数据初始化完成");
        }

        // 初始化学生数据
        if (studentMapper.selectCount(null) == 0) {
            logger.info("开始初始化学生数据...");

            Student[] students = {
                createStudent("2024001", "张三", 20, "男", "计算机科学与技术", "13800138001", "zhangsan@example.com"),
                createStudent("2024002", "李四", 21, "女", "软件工程", "13800138002", "lisi@example.com"),
                createStudent("2024003", "王五", 19, "男", "数据科学与大数据技术", "13800138003", "wangwu@example.com"),
                createStudent("2024004", "赵六", 22, "女", "人工智能", "13800138004", "zhaoliu@example.com"),
                createStudent("2024005", "孙七", 20, "男", "网络工程", "13800138005", "sunqi@example.com"),
                createStudent("2024006", "周八", 21, "女", "信息安全", "13800138006", "zhouba@example.com"),
                createStudent("2024007", "吴九", 19, "男", "物联网工程", "13800138007", "wujiu@example.com"),
                createStudent("2024008", "郑十", 22, "女", "计算机科学与技术", "13800138008", "zhengshi@example.com")
            };

            for (Student student : students) {
                studentMapper.insert(student);
            }

            logger.info("学生数据初始化完成，共插入 {} 条记录", students.length);
        }

        // 初始化课程数据
        if (courseMapper.selectCount(null) == 0) {
            logger.info("开始初始化课程数据...");

            Course[] courses = {
                createCourse("CS1001", "高等数学", 5.0, "李教授"),
                createCourse("CS1002", "大学英语", 4.0, "王教授"),
                createCourse("CS1003", "数据结构", 4.0, "张教授"),
                createCourse("CS1004", "操作系统", 3.5, "刘教授"),
                createCourse("CS1005", "计算机网络", 3.5, "陈教授"),
                createCourse("CS1006", "数据库原理", 4.0, "赵教授")
            };

            for (Course course : courses) {
                courseMapper.insert(course);
            }

            logger.info("课程数据初始化完成，共插入 {} 条记录", courses.length);
        }

        // 初始化成绩数据
        if (scoreMapper.selectCount(null) == 0) {
            logger.info("开始初始化成绩数据...");

            Double[][] regularScores = {
                {85.0, 78.0, 92.0, 88.0, 76.0, 90.0},
                {90.0, 85.0, 88.0, 92.0, 80.0, 85.0},
                {75.0, 82.0, 78.0, 70.0, 85.0, 72.0},
                {95.0, 92.0, 90.0, 94.0, 88.0, 92.0},
                {80.0, 75.0, 82.0, 78.0, 90.0, 80.0},
                {88.0, 90.0, 85.0, 82.0, 92.0, 88.0},
                {70.0, 68.0, 75.0, 72.0, 65.0, 70.0},
                {92.0, 88.0, 95.0, 90.0, 86.0, 94.0}
            };

            Double[][] finalScores = {
                {82.0, 80.0, 88.0, 85.0, 78.0, 86.0},
                {88.0, 90.0, 85.0, 90.0, 82.0, 88.0},
                {70.0, 78.0, 72.0, 68.0, 80.0, 65.0},
                {94.0, 95.0, 92.0, 96.0, 90.0, 94.0},
                {78.0, 72.0, 80.0, 75.0, 88.0, 78.0},
                {90.0, 92.0, 88.0, 85.0, 94.0, 90.0},
                {62.0, 60.0, 68.0, 65.0, 58.0, 62.0},
                {90.0, 85.0, 92.0, 88.0, 84.0, 90.0}
            };

            for (int studentIdx = 0; studentIdx < 8; studentIdx++) {
                for (int courseIdx = 0; courseIdx < 6; courseIdx++) {
                    Score score = new Score();
                    score.setStudentId((long) (studentIdx + 1));
                    score.setCourseId((long) (courseIdx + 1));
                    score.setRegularScore(regularScores[studentIdx][courseIdx]);
                    score.setFinalScore(finalScores[studentIdx][courseIdx]);
                    double total = regularScores[studentIdx][courseIdx] * 0.4 + finalScores[studentIdx][courseIdx] * 0.6;
                    score.setTotalScore(Math.round(total * 10.0) / 10.0);
                    score.setCreateTime(LocalDateTime.now());
                    score.setUpdateTime(LocalDateTime.now());
                    scoreMapper.insert(score);
                }
            }

            logger.info("成绩数据初始化完成");
        }
    }

    private Student createStudent(String studentNo, String name, int age, String gender, 
                                  String major, String phone, String email) {
        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setName(name);
        student.setAge(age);
        student.setGender(gender);
        student.setMajor(major);
        student.setPhone(phone);
        student.setEmail(email);
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        return student;
    }

    private Course createCourse(String courseNo, String courseName, Double credit, String teacher) {
        Course course = new Course();
        course.setCourseNo(courseNo);
        course.setCourseName(courseName);
        course.setCredit(credit);
        course.setTeacher(teacher);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        return course;
    }
}
