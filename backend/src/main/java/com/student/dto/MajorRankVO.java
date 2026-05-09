package com.student.dto;

public class MajorRankVO {

    private String major;
    private Double averageScore;
    private Double averageGpa;
    private Integer studentCount;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getAverageGpa() {
        return averageGpa;
    }

    public void setAverageGpa(Double averageGpa) {
        this.averageGpa = averageGpa;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}
