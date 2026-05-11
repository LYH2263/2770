package com.student.dto;

import jakarta.validation.constraints.NotNull;

public class ScoreInputDTO {
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private Double dailyScore;

    private Double finalScore;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Double getDailyScore() {
        return dailyScore;
    }

    public void setDailyScore(Double dailyScore) {
        this.dailyScore = dailyScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }
}
