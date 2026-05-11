package com.student.dto;

public class MajorRankDTO {
    private String major;
    private Double avgGpa;
    private Integer studentCount;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Double getAvgGpa() {
        return avgGpa;
    }

    public void setAvgGpa(Double avgGpa) {
        this.avgGpa = avgGpa;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}
