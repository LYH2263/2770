package com.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("students")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentNo;
    
    private String name;
    
    private Integer age;
    
    private String gender;
    
    private String major;
    
    private String phone;
    
    private String email;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
