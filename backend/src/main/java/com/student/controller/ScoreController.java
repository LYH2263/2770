package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ScoreBatchDTO;
import com.student.dto.ScoreDTO;
import com.student.dto.ScoreVO;
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

    @GetMapping("/page")
    public Result<IPage<ScoreVO>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String keyword) {
        IPage<ScoreVO> page = scoreService.getScorePage(pageNum, pageSize, studentId, courseId, keyword);
        return Result.success(page);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<ScoreVO>> getByStudentId(@PathVariable Long studentId) {
        List<ScoreVO> list = scoreService.getScoresByStudentId(studentId);
        return Result.success(list);
    }

    @GetMapping("/course/{courseId}")
    public Result<List<ScoreVO>> getByCourseId(@PathVariable Long courseId) {
        List<ScoreVO> list = scoreService.getScoresByCourseId(courseId);
        return Result.success(list);
    }

    @PostMapping
    public Result<String> save(@Valid @RequestBody ScoreDTO dto) {
        scoreService.saveScore(dto);
        return Result.success("录入成功");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody ScoreDTO dto) {
        scoreService.saveScore(dto);
        return Result.success("修改成功");
    }

    @PostMapping("/batch")
    public Result<String> batchSave(@RequestBody ScoreBatchDTO batchDTO) {
        scoreService.batchSaveScore(batchDTO);
        return Result.success("批量录入成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        scoreService.deleteScore(id);
        return Result.success("删除成功");
    }
}
