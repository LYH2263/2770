package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.student.dto.CourseStatisticsDTO;
import com.student.dto.MajorRankDTO;
import com.student.dto.ScoreDTO;
import com.student.dto.TopStudentDTO;
import com.student.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    @Select("SELECT s.id, s.student_id, s.course_id, s.regular_score, s.final_score, s.total_score, " +
            "st.student_no, st.name as student_name, st.major, " +
            "c.course_name, c.credit, c.teacher " +
            "FROM scores s " +
            "LEFT JOIN students st ON s.student_id = st.id " +
            "LEFT JOIN courses c ON s.course_id = c.id " +
            "WHERE s.student_id = #{studentId}")
    List<ScoreDTO> findScoresByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT s.id, s.student_id, s.course_id, s.regular_score, s.final_score, s.total_score, " +
            "st.student_no, st.name as student_name, st.major, " +
            "c.course_name, c.credit, c.teacher " +
            "FROM scores s " +
            "LEFT JOIN students st ON s.student_id = st.id " +
            "LEFT JOIN courses c ON s.course_id = c.id " +
            "WHERE s.course_id = #{courseId}")
    List<ScoreDTO> findScoresByCourseId(@Param("courseId") Long courseId);

    @Select("SELECT s.id, s.student_id, s.course_id, s.regular_score, s.final_score, s.total_score, " +
            "st.student_no, st.name as student_name, st.major, " +
            "c.course_name, c.credit, c.teacher " +
            "FROM scores s " +
            "LEFT JOIN students st ON s.student_id = st.id " +
            "LEFT JOIN courses c ON s.course_id = c.id " +
            "WHERE s.id = #{id}")
    ScoreDTO findScoreDetailById(@Param("id") Long id);

    @Select("SELECT c.id as course_id, c.course_name, " +
            "AVG(s.total_score) as avg_score, " +
            "MAX(s.total_score) as max_score, " +
            "MIN(s.total_score) as min_score, " +
            "COUNT(s.id) as total_count, " +
            "SUM(CASE WHEN s.total_score >= 60 THEN 1 ELSE 0 END) as pass_count, " +
            "SUM(CASE WHEN s.total_score < 60 THEN 1 ELSE 0 END) as fail_count, " +
            "ROUND(SUM(CASE WHEN s.total_score >= 60 THEN 1 ELSE 0 END) * 100.0 / COUNT(s.id), 2) as pass_rate " +
            "FROM courses c " +
            "LEFT JOIN scores s ON c.id = s.course_id " +
            "WHERE s.id IS NOT NULL " +
            "GROUP BY c.id, c.course_name")
    List<CourseStatisticsDTO> findCourseStatistics();

    @Select("SELECT st.id as student_id, st.student_no, st.name as student_name, st.major, " +
            "ROUND(AVG(s.total_score), 2) as avg_score, " +
            "ROUND(SUM(CASE " +
                "WHEN s.total_score >= 90 THEN 4.0 " +
                "WHEN s.total_score >= 85 THEN 3.7 " +
                "WHEN s.total_score >= 82 THEN 3.3 " +
                "WHEN s.total_score >= 78 THEN 3.0 " +
                "WHEN s.total_score >= 75 THEN 2.7 " +
                "WHEN s.total_score >= 72 THEN 2.3 " +
                "WHEN s.total_score >= 68 THEN 2.0 " +
                "WHEN s.total_score >= 64 THEN 1.5 " +
                "WHEN s.total_score >= 60 THEN 1.0 " +
                "ELSE 0 END * c.credit) / SUM(c.credit), 2) as gpa " +
            "FROM students st " +
            "LEFT JOIN scores s ON st.id = s.student_id " +
            "LEFT JOIN courses c ON s.course_id = c.id " +
            "WHERE s.id IS NOT NULL " +
            "GROUP BY st.id, st.student_no, st.name, st.major " +
            "ORDER BY gpa DESC " +
            "LIMIT 10")
    List<TopStudentDTO> findTopStudents();

    @Select("SELECT st.major, " +
            "ROUND(AVG(s.total_score), 2) as avg_score, " +
            "COUNT(DISTINCT st.id) as student_count " +
            "FROM students st " +
            "LEFT JOIN scores s ON st.id = s.student_id " +
            "WHERE s.id IS NOT NULL " +
            "GROUP BY st.major " +
            "ORDER BY avg_score DESC")
    List<MajorRankDTO> findMajorRanking();
}
