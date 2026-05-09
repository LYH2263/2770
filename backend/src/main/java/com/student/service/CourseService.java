package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.dto.CourseDTO;
import com.student.entity.Course;
import com.student.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    public IPage<Course> getCoursePage(int pageNum, int pageSize, String keyword) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Course::getCourseName, keyword)
                    .or()
                    .like(Course::getCourseNo, keyword)
                    .or()
                    .like(Course::getTeacher, keyword)
            );
        }
        wrapper.orderByDesc(Course::getCreateTime);
        return courseMapper.selectPage(page, wrapper);
    }

    public List<Course> getAllCourses() {
        return courseMapper.selectList(new LambdaQueryWrapper<Course>().orderByAsc(Course::getCourseNo));
    }

    public Course getCourseById(Long id) {
        return courseMapper.selectById(id);
    }

    public void saveCourse(CourseDTO dto) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseNo, dto.getCourseNo());
        if (dto.getId() != null) {
            wrapper.ne(Course::getId, dto.getId());
        }
        Long count = courseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("课程编号已存在");
        }

        Course course;
        if (dto.getId() != null) {
            course = courseMapper.selectById(dto.getId());
            if (course == null) {
                throw new IllegalArgumentException("课程不存在");
            }
        } else {
            course = new Course();
            course.setCreateTime(LocalDateTime.now());
        }
        course.setCourseNo(dto.getCourseNo());
        course.setCourseName(dto.getCourseName());
        course.setCredit(dto.getCredit());
        course.setTeacher(dto.getTeacher());
        course.setUpdateTime(LocalDateTime.now());

        if (dto.getId() == null) {
            courseMapper.insert(course);
        } else {
            courseMapper.updateById(course);
        }
    }

    public void deleteCourse(Long id) {
        courseMapper.deleteById(id);
    }
}
