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
    public Result<StudentTranscriptDTO> getStudentTranscript(@PathVariable Long studentId) {
        StudentTranscriptDTO transcript = statisticsService.getStudentTranscript(studentId);
        return Result.success(transcript);
    }

    @GetMapping("/courses")
    public Result<List<CourseStatisticsDTO>> getCourseStatistics() {
        List<CourseStatisticsDTO> list = statisticsService.getCourseStatistics();
        return Result.success(list);
    }

    @GetMapping("/top-students")
    public Result<List<TopStudentDTO>> getTopStudents() {
        List<TopStudentDTO> list = statisticsService.getTopStudents();
        return Result.success(list);
    }

    @GetMapping("/major-ranking")
    public Result<List<MajorRankDTO>> getMajorRanking() {
        List<MajorRankDTO> list = statisticsService.getMajorRanking();
        return Result.success(list);
    }

    @GetMapping("/overview")
    public Result<OverallStatisticsDTO> getOverallStatistics() {
        OverallStatisticsDTO statistics = statisticsService.getOverallStatistics();
        return Result.success(statistics);
    }
}
