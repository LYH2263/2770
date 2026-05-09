package com.student.dto;

public class DashboardVO {

    private Integer totalCourses;
    private Double averageGpa;
    private Double failRate;
    private java.util.List<TopStudentVO> topStudents;
    private java.util.List<MajorRankVO> majorRanks;
    private java.util.List<CourseStatisticsVO> courseStatistics;

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Double getAverageGpa() {
        return averageGpa;
    }

    public void setAverageGpa(Double averageGpa) {
        this.averageGpa = averageGpa;
    }

    public Double getFailRate() {
        return failRate;
    }

    public void setFailRate(Double failRate) {
        this.failRate = failRate;
    }

    public java.util.List<TopStudentVO> getTopStudents() {
        return topStudents;
    }

    public void setTopStudents(java.util.List<TopStudentVO> topStudents) {
        this.topStudents = topStudents;
    }

    public java.util.List<MajorRankVO> getMajorRanks() {
        return majorRanks;
    }

    public void setMajorRanks(java.util.List<MajorRankVO> majorRanks) {
        this.majorRanks = majorRanks;
    }

    public java.util.List<CourseStatisticsVO> getCourseStatistics() {
        return courseStatistics;
    }

    public void setCourseStatistics(java.util.List<CourseStatisticsVO> courseStatistics) {
        this.courseStatistics = courseStatistics;
    }
}
