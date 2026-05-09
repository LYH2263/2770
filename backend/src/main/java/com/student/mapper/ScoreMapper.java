package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.dto.CourseStatisticsVO;
import com.student.dto.MajorRankVO;
import com.student.dto.ScoreVO;
import com.student.dto.TopStudentVO;
import com.student.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    IPage<ScoreVO> selectScorePage(Page<ScoreVO> page,
                                    @Param("studentId") Long studentId,
                                    @Param("courseId") Long courseId,
                                    @Param("keyword") String keyword);

    List<ScoreVO> selectScoresByStudentId(@Param("studentId") Long studentId);

    List<ScoreVO> selectScoresByCourseId(@Param("courseId") Long courseId);

    List<CourseStatisticsVO> selectCourseStatistics();

    CourseStatisticsVO selectCourseStatisticsById(@Param("courseId") Long courseId);

    List<MajorRankVO> selectMajorRank();

    List<TopStudentVO> selectTopStudents(@Param("limit") int limit);

    Double selectAverageGpa();

    Double selectFailRate();
}
