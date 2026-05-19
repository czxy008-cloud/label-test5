package com.meeting.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.meeting.entity.dto.LoginDTO;
import com.meeting.service.UserService;
import com.meeting.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        StpUtil.logout();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<?> info() {
        return userService.getCurrentUser();
    }
}
