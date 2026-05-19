package com.meeting.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting.entity.User;
import com.meeting.entity.dto.LoginDTO;
import com.meeting.mapper.UserMapper;
import com.meeting.service.UserService;
import com.meeting.common.Result;
import org.springframework.stereotype.Service;
import cn.hutool.crypto.digest.BCrypt;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result<?> login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = getOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    @Override
    public Result<?> getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}
