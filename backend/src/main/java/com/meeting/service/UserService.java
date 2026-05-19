package com.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting.entity.User;
import com.meeting.entity.dto.LoginDTO;
import com.meeting.common.Result;

public interface UserService extends IService<User> {
    Result<?> login(LoginDTO loginDTO);
    Result<?> getCurrentUser();
}
