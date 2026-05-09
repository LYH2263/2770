package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.dto.ScoreBatchDTO;
import com.student.dto.ScoreDTO;
import com.student.dto.ScoreVO;
import com.student.entity.Score;
import com.student.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    public IPage<ScoreVO> getScorePage(int pageNum, int pageSize, Long studentId, Long courseId, String keyword) {
        Page<ScoreVO> page = new Page<>(pageNum, pageSize);
        return scoreMapper.selectScorePage(page, studentId, courseId, keyword);
    }

    public List<ScoreVO> getScoresByStudentId(Long studentId) {
        return scoreMapper.selectScoresByStudentId(studentId);
    }

    public List<ScoreVO> getScoresByCourseId(Long courseId) {
        return scoreMapper.selectScoresByCourseId(courseId);
    }

    public void saveScore(ScoreDTO dto) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId, dto.getStudentId());
        wrapper.eq(Score::getCourseId, dto.getCourseId());
        if (dto.getId() != null) {
            wrapper.ne(Score::getId, dto.getId());
        }
        Long count = scoreMapper.selectCount(wrapper);
        if (count > 0 && dto.getId() == null) {
            throw new IllegalArgumentException("该学生此课程成绩已存在");
        }

        Double usualScore = dto.getUsualScore() != null ? dto.getUsualScore() : 0;
        Double examScore = dto.getExamScore() != null ? dto.getExamScore() : 0;
        Double totalScore = usualScore * 0.3 + examScore * 0.7;

        Score score;
        if (dto.getId() != null) {
            score = scoreMapper.selectById(dto.getId());
            if (score == null) {
                throw new IllegalArgumentException("成绩记录不存在");
            }
        } else {
            score = new Score();
            score.setCreateTime(LocalDateTime.now());
        }
        score.setStudentId(dto.getStudentId());
        score.setCourseId(dto.getCourseId());
        score.setUsualScore(usualScore);
        score.setExamScore(examScore);
        score.setTotalScore(Math.round(totalScore * 100.0) / 100.0);
        score.setUpdateTime(LocalDateTime.now());

        if (dto.getId() == null) {
            scoreMapper.insert(score);
        } else {
            scoreMapper.updateById(score);
        }
    }

    public void batchSaveScore(ScoreBatchDTO batchDTO) {
        if (batchDTO.getScores() == null || batchDTO.getScores().isEmpty()) {
            throw new IllegalArgumentException("成绩列表不能为空");
        }
        for (ScoreBatchDTO.ScoreItemDTO item : batchDTO.getScores()) {
            LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Score::getStudentId, item.getStudentId());
            wrapper.eq(Score::getCourseId, batchDTO.getCourseId());
            Score existing = scoreMapper.selectOne(wrapper);

            Double usualScore = item.getUsualScore() != null ? item.getUsualScore() : 0;
            Double examScore = item.getExamScore() != null ? item.getExamScore() : 0;
            Double totalScore = Math.round((usualScore * 0.3 + examScore * 0.7) * 100.0) / 100.0;

            Score score;
            if (existing != null) {
                score = existing;
            } else {
                score = new Score();
                score.setStudentId(item.getStudentId());
                score.setCourseId(batchDTO.getCourseId());
                score.setCreateTime(LocalDateTime.now());
            }
            score.setUsualScore(usualScore);
            score.setExamScore(examScore);
            score.setTotalScore(totalScore);
            score.setUpdateTime(LocalDateTime.now());

            if (existing != null) {
                scoreMapper.updateById(score);
            } else {
                scoreMapper.insert(score);
            }
        }
    }

    public void deleteScore(Long id) {
        scoreMapper.deleteById(id);
    }
}
