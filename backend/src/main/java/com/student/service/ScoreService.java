package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.dto.ScoreDTO;
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

    public IPage<ScoreDTO> getScorePage(int pageNum, int pageSize, Long studentId, Long courseId) {
        Page<ScoreDTO> page = new Page<>(pageNum, pageSize);
        
        List<ScoreDTO> allScores;
        if (studentId != null) {
            allScores = scoreMapper.findScoresByStudentId(studentId);
        } else if (courseId != null) {
            allScores = scoreMapper.findScoresByCourseId(courseId);
        } else {
            LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(Score::getCreateTime);
            List<Score> scores = scoreMapper.selectList(wrapper);
            allScores = scores.stream().map(s -> scoreMapper.findScoreDetailById(s.getId())).toList();
        }

        int total = allScores.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<ScoreDTO> records = allScores.subList(start, end);

        page.setRecords(records);
        page.setTotal(total);
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return page;
    }

    public List<ScoreDTO> getScoresByStudentId(Long studentId) {
        return scoreMapper.findScoresByStudentId(studentId);
    }

    public List<ScoreDTO> getScoresByCourseId(Long courseId) {
        return scoreMapper.findScoresByCourseId(courseId);
    }

    public ScoreDTO getScoreById(Long id) {
        return scoreMapper.findScoreDetailById(id);
    }

    public void saveScore(ScoreDTO scoreDTO) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId, scoreDTO.getStudentId())
                .eq(Score::getCourseId, scoreDTO.getCourseId());
        if (scoreDTO.getId() != null) {
            wrapper.ne(Score::getId, scoreDTO.getId());
        }
        Long count = scoreMapper.selectCount(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("该学生此课程的成绩已存在");
        }

        Score score = new Score();
        score.setId(scoreDTO.getId());
        score.setStudentId(scoreDTO.getStudentId());
        score.setCourseId(scoreDTO.getCourseId());
        score.setRegularScore(scoreDTO.getRegularScore());
        score.setFinalScore(scoreDTO.getFinalScore());
        
        double regular = scoreDTO.getRegularScore() != null ? scoreDTO.getRegularScore() : 0;
        double finalScore = scoreDTO.getFinalScore() != null ? scoreDTO.getFinalScore() : 0;
        score.setTotalScore(regular * 0.4 + finalScore * 0.6);

        if (score.getId() == null) {
            score.setCreateTime(LocalDateTime.now());
        }
        score.setUpdateTime(LocalDateTime.now());

        if (score.getId() == null) {
            scoreMapper.insert(score);
        } else {
            scoreMapper.updateById(score);
        }
    }

    public void batchSaveScores(List<ScoreDTO> scoreDTOs) {
        for (ScoreDTO scoreDTO : scoreDTOs) {
            saveScore(scoreDTO);
        }
    }

    public void deleteScore(Long id) {
        scoreMapper.deleteById(id);
    }
}
