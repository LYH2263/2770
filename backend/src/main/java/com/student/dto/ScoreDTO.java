package com.student.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class ScoreDTO {
    private Long id;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    private String studentNo;

    private String studentName;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private String courseName;

    private String teacher;

    private Double credit;

    @DecimalMin(value = "0", message = "平时成绩不能小于0")
    @DecimalMax(value = "100", message = "平时成绩不能大于100")
    private Double regularScore;

    @DecimalMin(value = "0", message = "期末成绩不能小于0")
    @DecimalMax(value = "100", message = "期末成绩不能大于100")
    private Double finalScore;

    private Double totalScore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getRegularScore() {
        return regularScore;
    }

    public void setRegularScore(Double regularScore) {
        this.regularScore = regularScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
}
