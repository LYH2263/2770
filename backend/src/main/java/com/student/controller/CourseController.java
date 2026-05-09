package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.CourseDTO;
import com.student.entity.Course;
import com.student.service.CourseService;
import com.student.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/page")
    public Result<IPage<Course>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<Course> page = courseService.getCoursePage(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<List<Course>> getList() {
        List<Course> list = courseService.getAllCourses();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return Result.success(course);
    }

    @PostMapping
    public Result<String> save(@Valid @RequestBody CourseDTO dto) {
        courseService.saveCourse(dto);
        return Result.success("保存成功");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody CourseDTO dto) {
        courseService.saveCourse(dto);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success("删除成功");
    }
}
