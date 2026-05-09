package com.student.controller;

import com.student.dto.LoginRequest;
import com.student.dto.LoginResponse;
import com.student.entity.User;
import com.student.service.UserService;
import com.student.util.JwtUtil;
import com.student.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse response = new LoginResponse(token, user.getUsername(), user.getNickname());
        return Result.success("登录成功", response);
    }
}
