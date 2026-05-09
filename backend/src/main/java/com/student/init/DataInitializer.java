package com.student.init;

import com.student.entity.Student;
import com.student.entity.User;
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
}
