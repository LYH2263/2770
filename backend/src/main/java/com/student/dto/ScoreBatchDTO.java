package com.student.dto;

import java.util.List;

public class ScoreBatchDTO {

    private Long courseId;

    private List<ScoreItemDTO> scores;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<ScoreItemDTO> getScores() {
        return scores;
    }

    public void setScores(List<ScoreItemDTO> scores) {
        this.scores = scores;
    }

    public static class ScoreItemDTO {
        private Long studentId;
        private Double usualScore;
        private Double examScore;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Double getUsualScore() {
            return usualScore;
        }

        public void setUsualScore(Double usualScore) {
            this.usualScore = usualScore;
        }

        public Double getExamScore() {
            return examScore;
        }

        public void setExamScore(Double examScore) {
            this.examScore = examScore;
        }
    }
}
