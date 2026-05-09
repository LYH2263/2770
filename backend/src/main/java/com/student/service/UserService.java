package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.entity.User;
import com.student.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("密码错误");
        }
        return user;
    }
}
