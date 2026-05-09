package com.student.dto;

public class StudentTranscriptVO {

    private Long studentId;
    private String studentNo;
    private String studentName;
    private String major;
    private Double gpa;
    private java.util.List<CourseScoreVO> courses;

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

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public java.util.List<CourseScoreVO> getCourses() {
        return courses;
    }

    public void setCourses(java.util.List<CourseScoreVO> courses) {
        this.courses = courses;
    }

    public static class CourseScoreVO {
        private String courseName;
        private Double credit;
        private Double totalScore;
        private String gradePoint;
        private String level;

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public Double getCredit() {
            return credit;
        }

        public void setCredit(Double credit) {
            this.credit = credit;
        }

        public Double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(Double totalScore) {
            this.totalScore = totalScore;
        }

        public String getGradePoint() {
            return gradePoint;
        }

        public void setGradePoint(String gradePoint) {
            this.gradePoint = gradePoint;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
    }
}
