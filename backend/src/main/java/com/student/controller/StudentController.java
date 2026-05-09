package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.entity.Student;
import com.student.service.StudentService;
import com.student.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/page")
    public Result<IPage<Student>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<Student> page = studentService.getStudentPage(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<List<Student>> getList() {
        List<Student> list = studentService.getAllStudents();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return Result.success(student);
    }

    @PostMapping
    public Result<String> save(@Valid @RequestBody Student student) {
        studentService.saveStudent(student);
        return Result.success("保存成功");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody Student student) {
        studentService.saveStudent(student);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return Result.success("删除成功");
    }
}
