package com.student.controller;

import com.student.dto.*;
import com.student.service.StatisticsService;
import com.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/transcript/{studentId}")
    public Result<StudentTranscriptVO> getStudentTranscript(@PathVariable Long studentId) {
        StudentTranscriptVO transcript = statisticsService.getStudentTranscript(studentId);
        return Result.success(transcript);
    }

    @GetMapping("/course")
    public Result<List<CourseStatisticsVO>> getCourseStatistics() {
        List<CourseStatisticsVO> list = statisticsService.getCourseStatistics();
        return Result.success(list);
    }

    @GetMapping("/course/{courseId}")
    public Result<CourseStatisticsVO> getCourseStatisticsById(@PathVariable Long courseId) {
        CourseStatisticsVO vo = statisticsService.getCourseStatisticsById(courseId);
        return Result.success(vo);
    }

    @GetMapping("/major-rank")
    public Result<List<MajorRankVO>> getMajorRank() {
        List<MajorRankVO> list = statisticsService.getMajorRank();
        return Result.success(list);
    }

    @GetMapping("/top-students")
    public Result<List<TopStudentVO>> getTopStudents(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopStudentVO> list = statisticsService.getTopStudents(limit);
        return Result.success(list);
    }

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO dashboard = statisticsService.getDashboard();
        return Result.success(dashboard);
    }
}
