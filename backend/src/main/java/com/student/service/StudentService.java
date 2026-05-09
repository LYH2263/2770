package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.entity.Student;
import com.student.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    public IPage<Student> getStudentPage(int pageNum, int pageSize, String keyword) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Student::getName, keyword)
                    .or()
                    .like(Student::getStudentNo, keyword)
                    .or()
                    .like(Student::getMajor, keyword)
            );
        }
        
        wrapper.orderByDesc(Student::getCreateTime);
        return studentMapper.selectPage(page, wrapper);
    }

    public List<Student> getAllStudents() {
        return studentMapper.selectList(null);
    }

    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }

    public void saveStudent(Student student) {
        // 检查学号是否重复
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, student.getStudentNo());
        if (student.getId() != null) {
            wrapper.ne(Student::getId, student.getId());
        }
        Long count = studentMapper.selectCount(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("学号已存在");
        }

        if (student.getId() == null) {
            student.setCreateTime(LocalDateTime.now());
        }
        student.setUpdateTime(LocalDateTime.now());

        if (student.getId() == null) {
            studentMapper.insert(student);
        } else {
            studentMapper.updateById(student);
        }
    }

    public void deleteStudent(Long id) {
        studentMapper.deleteById(id);
    }
}
