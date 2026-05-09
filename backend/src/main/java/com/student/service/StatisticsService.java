package com.student.service;

import com.student.dto.*;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private CourseMapper courseMapper;

    public StudentTranscriptVO getStudentTranscript(Long studentId) {
        List<ScoreVO> scores = scoreMapper.selectScoresByStudentId(studentId);
        if (scores.isEmpty()) {
            throw new IllegalArgumentException("该学生暂无成绩记录");
        }

        StudentTranscriptVO transcript = new StudentTranscriptVO();
        ScoreVO first = scores.get(0);
        transcript.setStudentId(first.getStudentId());
        transcript.setStudentNo(first.getStudentNo());
        transcript.setStudentName(first.getStudentName());

        double totalGradePoints = 0;
        double totalCredits = 0;

        java.util.List<StudentTranscriptVO.CourseScoreVO> courseScores = new java.util.ArrayList<>();
        for (ScoreVO score : scores) {
            StudentTranscriptVO.CourseScoreVO cs = new StudentTranscriptVO.CourseScoreVO();
            cs.setCourseName(score.getCourseName());
            cs.setCredit(score.getCredit());
            cs.setTotalScore(score.getTotalScore());
            cs.setGradePoint(String.valueOf(convertToGradePoint(score.getTotalScore())));
            cs.setLevel(convertToLevel(score.getTotalScore()));
            courseScores.add(cs);

            double gp = convertToGradePoint(score.getTotalScore());
            totalGradePoints += gp * score.getCredit();
            totalCredits += score.getCredit();
        }

        transcript.setCourses(courseScores);
        transcript.setMajor(first.getMajor() != null ? first.getMajor() : "");
        transcript.setGpa(totalCredits > 0 ? Math.round(totalGradePoints / totalCredits * 100.0) / 100.0 : 0);

        return transcript;
    }

    public List<CourseStatisticsVO> getCourseStatistics() {
        return scoreMapper.selectCourseStatistics();
    }

    public CourseStatisticsVO getCourseStatisticsById(Long courseId) {
        return scoreMapper.selectCourseStatisticsById(courseId);
    }

    public List<MajorRankVO> getMajorRank() {
        return scoreMapper.selectMajorRank();
    }

    public List<TopStudentVO> getTopStudents(int limit) {
        return scoreMapper.selectTopStudents(limit);
    }

    public DashboardVO getDashboard() {
        DashboardVO dashboard = new DashboardVO();

        Long courseCount = courseMapper.selectCount(null);
        dashboard.setTotalCourses(courseCount.intValue());

        Double avgGpa = scoreMapper.selectAverageGpa();
        dashboard.setAverageGpa(avgGpa != null ? avgGpa : 0);

        Double failRate = scoreMapper.selectFailRate();
        dashboard.setFailRate(failRate != null ? failRate : 0);

        dashboard.setTopStudents(scoreMapper.selectTopStudents(10));
        dashboard.setMajorRanks(scoreMapper.selectMajorRank());
        dashboard.setCourseStatistics(scoreMapper.selectCourseStatistics());

        return dashboard;
    }

    private double convertToGradePoint(Double score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0;
    }

    private String convertToLevel(Double score) {
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 70) return "中等";
        if (score >= 60) return "及格";
        return "不及格";
    }
}
