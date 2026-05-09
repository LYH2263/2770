package com.student.dto;

import java.util.List;

public class OverallStatisticsDTO {
    private Integer totalCourses;
    private Integer totalStudents;
    private Integer totalScores;
    private Double avgGpa;
    private Double failRate;
    private Double avgScore;
    private List<TopStudentDTO> topStudents;
    private List<CourseStatisticsDTO> courseStatistics;

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Integer getTotalScores() {
        return totalScores;
    }

    public void setTotalScores(Integer totalScores) {
        this.totalScores = totalScores;
    }

    public Double getAvgGpa() {
        return avgGpa;
    }

    public void setAvgGpa(Double avgGpa) {
        this.avgGpa = avgGpa;
    }

    public Double getFailRate() {
        return failRate;
    }

    public void setFailRate(Double failRate) {
        this.failRate = failRate;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    public List<TopStudentDTO> getTopStudents() {
        return topStudents;
    }

    public void setTopStudents(List<TopStudentDTO> topStudents) {
        this.topStudents = topStudents;
    }

    public List<CourseStatisticsDTO> getCourseStatistics() {
        return courseStatistics;
    }

    public void setCourseStatistics(List<CourseStatisticsDTO> courseStatistics) {
        this.courseStatistics = courseStatistics;
    }
}
