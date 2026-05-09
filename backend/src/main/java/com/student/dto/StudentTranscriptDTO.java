package com.student.dto;

import java.util.List;

public class StudentTranscriptDTO {
    private Long studentId;
    private String studentNo;
    private String studentName;
    private String major;
    private List<ScoreDTO> scores;
    private Double gpa;
    private Double totalCredit;
    private Double earnedCredit;
    private Integer totalCourses;
    private Integer passCourses;
    private Double avgScore;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<ScoreDTO> getScores() {
        return scores;
    }

    public void setScores(List<ScoreDTO> scores) {
        this.scores = scores;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Double getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Double totalCredit) {
        this.totalCredit = totalCredit;
    }

    public Double getEarnedCredit() {
        return earnedCredit;
    }

    public void setEarnedCredit(Double earnedCredit) {
        this.earnedCredit = earnedCredit;
    }

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Integer getPassCourses() {
        return passCourses;
    }

    public void setPassCourses(Integer passCourses) {
        this.passCourses = passCourses;
    }

    public Double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }
}
