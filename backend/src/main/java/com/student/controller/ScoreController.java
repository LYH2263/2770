package com.student.controller;

import com.student.dto.*;
import com.student.service.ScoreService;
import com.student.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping
    public Result<List<ScoreDTO>> getScores(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String keyword) {
        List<ScoreDTO> list = scoreService.getScores(studentId, courseId, keyword);
        return Result.success(list);
    }

    @PostMapping
    public Result<String> save(@Valid @RequestBody ScoreInputDTO inputDTO) {
        scoreService.saveScore(inputDTO);
        return Result.success("保存成功");
    }

    @PostMapping("/batch")
    public Result<String> batchSave(@Valid @RequestBody List<ScoreInputDTO> inputDTOList) {
        scoreService.batchSaveScores(inputDTOList);
        return Result.success("批量保存成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        scoreService.deleteScore(id);
        return Result.success("删除成功");
    }

    @GetMapping("/transcript/{studentId}")
    public Result<StudentTranscriptDTO> getTranscript(@PathVariable Long studentId) {
        StudentTranscriptDTO transcript = scoreService.getStudentTranscript(studentId);
        return Result.success(transcript);
    }

    @GetMapping("/statistics/course/{courseId}")
    public Result<CourseStatisticsDTO> getCourseStatistics(@PathVariable Long courseId) {
        CourseStatisticsDTO statistics = scoreService.getCourseStatistics(courseId);
        return Result.success(statistics);
    }

    @GetMapping("/statistics/major-rank")
    public Result<List<MajorRankDTO>> getMajorRank() {
        List<MajorRankDTO> list = scoreService.getMajorRank();
        return Result.success(list);
    }

    @GetMapping("/statistics/dashboard")
    public Result<DashboardStatisticsDTO> getDashboardStatistics() {
        DashboardStatisticsDTO statistics = scoreService.getDashboardStatistics();
        return Result.success(statistics);
    }
}
