package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.dto.*;
import com.student.entity.Course;
import com.student.entity.Score;
import com.student.entity.Student;
import com.student.mapper.CourseMapper;
import com.student.mapper.ScoreMapper;
import com.student.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    public List<ScoreDTO> getScores(Long studentId, Long courseId, String keyword) {
        List<Score> scores;
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();

        if (studentId != null) {
            wrapper.eq(Score::getStudentId, studentId);
        }
        if (courseId != null) {
            wrapper.eq(Score::getCourseId, courseId);
        }

        scores = scoreMapper.selectList(wrapper);

        List<Student> students = studentMapper.selectList(null);
        List<Course> courses = courseMapper.selectList(null);

        Map<Long, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        return scores.stream()
                .map(score -> convertToDTO(score, studentMap, courseMap))
                .filter(dto -> {
                    if (!StringUtils.hasText(keyword)) return true;
                    return dto.getStudentName().contains(keyword)
                            || dto.getStudentNo().contains(keyword)
                            || dto.getCourseName().contains(keyword);
                })
                .collect(Collectors.toList());
    }

    public void saveScore(ScoreInputDTO inputDTO) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId, inputDTO.getStudentId());
        wrapper.eq(Score::getCourseId, inputDTO.getCourseId());
        Score existingScore = scoreMapper.selectOne(wrapper);

        Score score = new Score();
        if (existingScore != null) {
            score.setId(existingScore.getId());
        }
        score.setStudentId(inputDTO.getStudentId());
        score.setCourseId(inputDTO.getCourseId());
        score.setDailyScore(inputDTO.getDailyScore());
        score.setFinalScore(inputDTO.getFinalScore());

        Double daily = inputDTO.getDailyScore() != null ? inputDTO.getDailyScore() : 0.0;
        Double finalScore = inputDTO.getFinalScore() != null ? inputDTO.getFinalScore() : 0.0;
        score.setTotalScore(daily * 0.3 + finalScore * 0.7);

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

    public void batchSaveScores(List<ScoreInputDTO> inputDTOList) {
        for (ScoreInputDTO dto : inputDTOList) {
            saveScore(dto);
        }
    }

    public void deleteScore(Long id) {
        scoreMapper.deleteById(id);
    }

    public StudentTranscriptDTO getStudentTranscript(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在");
        }

        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId, studentId);
        List<Score> scores = scoreMapper.selectList(wrapper);

        List<Course> courses = courseMapper.selectList(null);
        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));
        Map<Long, Student> studentMap = new HashMap<>();
        studentMap.put(studentId, student);

        List<ScoreDTO> scoreDTOList = scores.stream()
                .map(score -> convertToDTO(score, studentMap, courseMap))
                .collect(Collectors.toList());

        double totalWeightedScore = 0;
        int totalCredits = 0;
        for (ScoreDTO dto : scoreDTOList) {
            if (dto.getCredit() != null && dto.getTotalScore() != null) {
                totalWeightedScore += calculateGPA(dto.getTotalScore()) * dto.getCredit();
                totalCredits += dto.getCredit();
            }
        }

        StudentTranscriptDTO transcript = new StudentTranscriptDTO();
        transcript.setStudentId(studentId);
        transcript.setStudentNo(student.getStudentNo());
        transcript.setStudentName(student.getName());
        transcript.setMajor(student.getMajor());
        transcript.setScores(scoreDTOList);
        transcript.setTotalCredits(totalCredits);
        transcript.setGpa(totalCredits > 0 ? totalWeightedScore / totalCredits : 0.0);

        return transcript;
    }

    public CourseStatisticsDTO getCourseStatistics(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }

        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getCourseId, courseId);
        List<Score> scores = scoreMapper.selectList(wrapper);

        if (scores.isEmpty()) {
            CourseStatisticsDTO stats = new CourseStatisticsDTO();
            stats.setCourseId(courseId);
            stats.setCourseName(course.getCourseName());
            stats.setStudentCount(0);
            return stats;
        }

        DoubleSummaryStatistics stats = scores.stream()
                .filter(s -> s.getTotalScore() != null)
                .mapToDouble(Score::getTotalScore)
                .summaryStatistics();

        long passCount = scores.stream()
                .filter(s -> s.getTotalScore() != null && s.getTotalScore() >= 60)
                .count();

        CourseStatisticsDTO result = new CourseStatisticsDTO();
        result.setCourseId(courseId);
        result.setCourseName(course.getCourseName());
        result.setStudentCount(scores.size());
        result.setAvgScore(stats.getAverage());
        result.setMaxScore(stats.getMax());
        result.setMinScore(stats.getMin());
        result.setPassRate((double) passCount / scores.size() * 100);

        return result;
    }

    public List<MajorRankDTO> getMajorRank() {
        List<Student> students = studentMapper.selectList(null);
        List<Score> scores = scoreMapper.selectList(null);
        List<Course> courses = courseMapper.selectList(null);

        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        Map<Long, List<Score>> studentScoresMap = scores.stream()
                .collect(Collectors.groupingBy(Score::getStudentId));

        Map<String, List<Double>> majorGpaMap = new HashMap<>();

        for (Student student : students) {
            List<Score> studentScores = studentScoresMap.getOrDefault(student.getId(), Collections.emptyList());
            double studentGpa = calculateStudentGpa(studentScores, courseMap);

            if (student.getMajor() != null) {
                majorGpaMap.computeIfAbsent(student.getMajor(), k -> new ArrayList<>())
                        .add(studentGpa);
            }
        }

        return majorGpaMap.entrySet().stream()
                .map(entry -> {
                    MajorRankDTO dto = new MajorRankDTO();
                    dto.setMajor(entry.getKey());
                    dto.setStudentCount(entry.getValue().size());
                    dto.setAvgGpa(entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getAvgGpa(), a.getAvgGpa()))
                .collect(Collectors.toList());
    }

    public DashboardStatisticsDTO getDashboardStatistics() {
        Long totalCourses = courseMapper.selectCount(null);
        List<Score> scores = scoreMapper.selectList(null);
        List<Student> students = studentMapper.selectList(null);
        List<Course> courses = courseMapper.selectList(null);

        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        Map<Long, List<Score>> studentScoresMap = scores.stream()
                .collect(Collectors.groupingBy(Score::getStudentId));

        List<Double> allGpas = new ArrayList<>();
        for (Student student : students) {
            List<Score> studentScores = studentScoresMap.getOrDefault(student.getId(), Collections.emptyList());
            double gpa = calculateStudentGpa(studentScores, courseMap);
            if (gpa > 0) {
                allGpas.add(gpa);
            }
        }

        double avgGpa = allGpas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        long failCount = scores.stream()
                .filter(s -> s.getTotalScore() != null && s.getTotalScore() < 60)
                .count();
        double failRate = scores.size() > 0 ? (double) failCount / scores.size() * 100 : 0.0;

        List<TopStudentDTO> topStudents = students.stream()
                .map(student -> {
                    List<Score> studentScores = studentScoresMap.getOrDefault(student.getId(), Collections.emptyList());
                    TopStudentDTO dto = new TopStudentDTO();
                    dto.setStudentId(student.getId());
                    dto.setStudentNo(student.getStudentNo());
                    dto.setStudentName(student.getName());
                    dto.setMajor(student.getMajor());
                    dto.setGpa(calculateStudentGpa(studentScores, courseMap));
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getGpa(), a.getGpa()))
                .limit(10)
                .collect(Collectors.toList());

        for (int i = 0; i < topStudents.size(); i++) {
            topStudents.get(i).setRank(i + 1);
        }

        DashboardStatisticsDTO dashboard = new DashboardStatisticsDTO();
        dashboard.setTotalCourses(totalCourses);
        dashboard.setAvgGpa(avgGpa);
        dashboard.setFailRate(failRate);
        dashboard.setTopStudents(topStudents);

        return dashboard;
    }

    private ScoreDTO convertToDTO(Score score, Map<Long, Student> studentMap, Map<Long, Course> courseMap) {
        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        dto.setStudentId(score.getStudentId());
        dto.setCourseId(score.getCourseId());
        dto.setDailyScore(score.getDailyScore());
        dto.setFinalScore(score.getFinalScore());
        dto.setTotalScore(score.getTotalScore());

        Student student = studentMap.get(score.getStudentId());
        if (student != null) {
            dto.setStudentNo(student.getStudentNo());
            dto.setStudentName(student.getName());
        }

        Course course = courseMap.get(score.getCourseId());
        if (course != null) {
            dto.setCourseNo(course.getCourseNo());
            dto.setCourseName(course.getCourseName());
            dto.setCredit(course.getCredit());
        }

        return dto;
    }

    private double calculateGPA(double score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0.0;
    }

    private double calculateStudentGpa(List<Score> scores, Map<Long, Course> courseMap) {
        double totalWeightedScore = 0;
        int totalCredits = 0;
        for (Score score : scores) {
            Course course = courseMap.get(score.getCourseId());
            if (course != null && score.getTotalScore() != null) {
                totalWeightedScore += calculateGPA(score.getTotalScore()) * course.getCredit();
                totalCredits += course.getCredit();
            }
        }
        return totalCredits > 0 ? totalWeightedScore / totalCredits : 0.0;
    }
}
