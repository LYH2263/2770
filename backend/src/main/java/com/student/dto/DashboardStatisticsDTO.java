package com.student.dto;

import java.util.List;

public class DashboardStatisticsDTO {
    private Long totalCourses;
    private Double avgGpa;
    private Double failRate;
    private List<TopStudentDTO> topStudents;

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
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

    public List<TopStudentDTO> getTopStudents() {
        return topStudents;
    }

    public void setTopStudents(List<TopStudentDTO> topStudents) {
        this.topStudents = topStudents;
    }
}
