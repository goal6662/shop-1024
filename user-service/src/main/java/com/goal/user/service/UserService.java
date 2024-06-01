package com.goal.user.service;

import com.goal.user.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.user.domain.dto.UserLoginDTO;
import com.goal.user.domain.dto.UserRegisterDTO;
import com.goal.utils.Result;

/**
* @author Goal
* @description 针对表【user】的数据库操作Service
* @createDate 2024-05-28 23:27:37
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    Result register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    Result<String> login(UserLoginDTO loginDTO);
}
