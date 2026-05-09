package com.student.service;

import com.student.dto.*;
import com.student.entity.Course;
import com.student.entity.Student;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    public StudentTranscriptDTO getStudentTranscript(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在");
        }

        List<ScoreDTO> scores = scoreMapper.findScoresByStudentId(studentId);

        StudentTranscriptDTO transcript = new StudentTranscriptDTO();
        transcript.setStudentId(student.getId());
        transcript.setStudentNo(student.getStudentNo());
        transcript.setStudentName(student.getName());
        transcript.setMajor(student.getMajor());
        transcript.setScores(scores);
        transcript.setTotalCourses(scores.size());

        double totalCredit = 0;
        double earnedCredit = 0;
        double totalScore = 0;
        double weightedScore = 0;
        int passCount = 0;

        for (ScoreDTO score : scores) {
            double credit = score.getCredit() != null ? score.getCredit() : 0;
            double totalScoreVal = score.getTotalScore() != null ? score.getTotalScore() : 0;

            totalCredit += credit;
            totalScore += totalScoreVal;

            if (totalScoreVal >= 60) {
                earnedCredit += credit;
                passCount++;
            }

            double gpaPoint = calculateGpaPoint(totalScoreVal);
            weightedScore += gpaPoint * credit;
        }

        transcript.setTotalCredit(totalCredit);
        transcript.setEarnedCredit(earnedCredit);
        transcript.setPassCourses(passCount);
        transcript.setAvgScore(scores.size() > 0 ? Math.round(totalScore / scores.size() * 100.0) / 100.0 : 0);
        transcript.setGpa(totalCredit > 0 ? Math.round(weightedScore / totalCredit * 100.0) / 100.0 : 0);

        return transcript;
    }

    private double calculateGpaPoint(double score) {
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

    public List<CourseStatisticsDTO> getCourseStatistics() {
        return scoreMapper.findCourseStatistics();
    }

    public List<TopStudentDTO> getTopStudents() {
        List<TopStudentDTO> students = scoreMapper.findTopStudents();
        for (int i = 0; i < students.size(); i++) {
            students.get(i).setRank(i + 1);
        }
        return students;
    }

    public List<MajorRankDTO> getMajorRanking() {
        List<MajorRankDTO> majors = scoreMapper.findMajorRanking();
        for (int i = 0; i < majors.size(); i++) {
            majors.get(i).setRank(i + 1);
        }
        return majors;
    }

    public OverallStatisticsDTO getOverallStatistics() {
        OverallStatisticsDTO statistics = new OverallStatisticsDTO();

        Integer totalStudents = Math.toIntExact(studentMapper.selectCount(null));
        Integer totalCourses = Math.toIntExact(courseMapper.selectCount(null));
        Integer totalScores = Math.toIntExact(scoreMapper.selectCount(null));

        statistics.setTotalStudents(totalStudents);
        statistics.setTotalCourses(totalCourses);
        statistics.setTotalScores(totalScores);

        List<TopStudentDTO> topStudents = getTopStudents();
        statistics.setTopStudents(topStudents);

        List<CourseStatisticsDTO> courseStats = getCourseStatistics();
        statistics.setCourseStatistics(courseStats);

        if (!courseStats.isEmpty()) {
            double totalAvg = courseStats.stream()
                    .mapToDouble(s -> s.getAvgScore() != null ? s.getAvgScore() : 0)
                    .average()
                    .orElse(0);
            statistics.setAvgScore(Math.round(totalAvg * 100.0) / 100.0);

            double totalFailRate = courseStats.stream()
                    .mapToInt(s -> s.getFailCount() != null ? s.getFailCount() : 0)
                    .sum();
            statistics.setFailRate(totalScores > 0 ? Math.round(totalFailRate * 10000.0 / totalScores) / 100.0 : 0);
        }

        if (!topStudents.isEmpty()) {
            double totalGpa = topStudents.stream()
                    .mapToDouble(s -> s.getGpa() != null ? s.getGpa() : 0)
                    .average()
                    .orElse(0);
            statistics.setAvgGpa(Math.round(totalGpa * 100.0) / 100.0);
        }

        return statistics;
    }
}
