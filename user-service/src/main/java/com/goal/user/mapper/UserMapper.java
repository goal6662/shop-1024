package com.goal.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goal.user.domain.User;

/**
* @author Goal
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-05-28 23:27:37
* @Entity com.goal.user.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
    User getUserByMail(String mail);
}




