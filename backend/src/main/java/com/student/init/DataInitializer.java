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

        if (courseMapper.selectCount(null) == 0) {
            logger.info("开始初始化课程数据...");

            Course[] courses = {
                createCourse("CS101", "高等数学", 4.0, "李教授"),
                createCourse("CS102", "线性代数", 3.0, "王教授"),
                createCourse("CS103", "大学物理", 3.0, "张教授"),
                createCourse("CS104", "程序设计基础", 4.0, "刘教授"),
                createCourse("CS105", "数据结构", 3.5, "陈教授"),
                createCourse("CS106", "计算机网络", 3.0, "赵教授")
            };

            for (Course course : courses) {
                courseMapper.insert(course);
            }

            logger.info("课程数据初始化完成，共插入 {} 条记录", courses.length);
        }

        if (scoreMapper.selectCount(null) == 0) {
            logger.info("开始初始化成绩数据...");

            Object[][] scoreData = {
                {1L, 1L, 85.0, 88.0}, {1L, 2L, 90.0, 82.0}, {1L, 3L, 78.0, 85.0},
                {1L, 4L, 92.0, 95.0}, {1L, 5L, 88.0, 90.0}, {1L, 6L, 75.0, 80.0},
                {2L, 1L, 82.0, 78.0}, {2L, 2L, 88.0, 90.0}, {2L, 3L, 76.0, 72.0},
                {2L, 4L, 95.0, 92.0}, {2L, 5L, 80.0, 85.0}, {2L, 6L, 90.0, 88.0},
                {3L, 1L, 70.0, 65.0}, {3L, 2L, 75.0, 68.0}, {3L, 3L, 80.0, 82.0},
                {3L, 4L, 85.0, 88.0}, {3L, 5L, 72.0, 70.0}, {3L, 6L, 68.0, 55.0},
                {4L, 1L, 95.0, 92.0}, {4L, 2L, 92.0, 88.0}, {4L, 3L, 88.0, 90.0},
                {4L, 4L, 90.0, 85.0}, {4L, 5L, 94.0, 96.0}, {4L, 6L, 86.0, 82.0},
                {5L, 1L, 60.0, 55.0}, {5L, 2L, 65.0, 58.0}, {5L, 3L, 70.0, 62.0},
                {5L, 4L, 78.0, 72.0}, {5L, 5L, 72.0, 68.0}, {5L, 6L, 55.0, 45.0},
                {6L, 1L, 88.0, 85.0}, {6L, 2L, 82.0, 78.0}, {6L, 3L, 90.0, 92.0},
                {6L, 4L, 85.0, 80.0}, {6L, 5L, 78.0, 82.0}, {6L, 6L, 92.0, 90.0},
                {7L, 1L, 75.0, 70.0}, {7L, 2L, 80.0, 76.0}, {7L, 3L, 68.0, 62.0},
                {7L, 4L, 82.0, 85.0}, {7L, 5L, 70.0, 65.0}, {7L, 6L, 78.0, 72.0},
                {8L, 1L, 90.0, 88.0}, {8L, 2L, 85.0, 82.0}, {8L, 3L, 92.0, 90.0},
                {8L, 4L, 88.0, 85.0}, {8L, 5L, 80.0, 78.0}, {8L, 6L, 95.0, 93.0}
            };

            int count = 0;
            for (Object[] data : scoreData) {
                Long studentId = (Long) data[0];
                Long courseId = (Long) data[1];
                Double usual = (Double) data[2];
                Double exam = (Double) data[3];
                Double total = Math.round((usual * 0.3 + exam * 0.7) * 100.0) / 100.0;
                scoreMapper.insert(createScore(studentId, courseId, usual, exam, total));
                count++;
            }

            logger.info("成绩数据初始化完成，共插入 {} 条记录", count);
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

    private Score createScore(Long studentId, Long courseId, Double usualScore, Double examScore, Double totalScore) {
        Score score = new Score();
        score.setStudentId(studentId);
        score.setCourseId(courseId);
        score.setUsualScore(usualScore);
        score.setExamScore(examScore);
        score.setTotalScore(totalScore);
        score.setCreateTime(LocalDateTime.now());
        score.setUpdateTime(LocalDateTime.now());
        return score;
    }
}
