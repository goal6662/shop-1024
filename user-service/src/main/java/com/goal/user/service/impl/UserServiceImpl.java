package com.goal.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.user.domain.User;
import com.goal.user.service.UserService;
import com.goal.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Goal
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-05-28 23:27:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




