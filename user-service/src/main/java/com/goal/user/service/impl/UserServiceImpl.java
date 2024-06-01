package com.goal.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.SendCodeEnum;
import com.goal.user.domain.User;
import com.goal.user.domain.dto.UserRegisterDTO;
import com.goal.user.service.NotifyService;
import com.goal.user.service.UserService;
import com.goal.user.mapper.UserMapper;
import com.goal.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author Goal
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-05-28 23:27:37
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private NotifyService notifyService;

    @Resource
    private UserMapper userMapper;

    /**
     * 邮箱验证码验证
     * 账号唯一性检查
     * 密码加密
     * 写入数据库
     * 新用户福利发放
     * @param registerDTO
     * @return
     */
    @Override
    public Result register(UserRegisterDTO registerDTO) {

        String email = registerDTO.getEmail();
        if (StringUtils.isBlank(email)
                || !notifyService.checkCode(SendCodeEnum.USER_REGISTER, email, registerDTO.getCode())) {
            return Result.fail(BizCodeEnum.CODE_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);

        user.setCreateTime(new Date());

        // TODO: 2024/5/31 账号唯一性检查
        boolean isUnique = checkUnique(registerDTO.getEmail());
        // TODO: 2024/5/31 设置密码
        boolean setP;

        if (this.save(user)) {
            log.info("用户注册成功：{}", user);

            // TODO: 2024/5/31 发放新用户福利
            userRegisterInitTask(user);
            return Result.success();
        }

        return Result.fail(BizCodeEnum.OPS_ERROR);
    }

    /**
     * 发放新用户福利
     * @param user 新用户
     */
    private void userRegisterInitTask(User user) {
    }

    /**
     * 检查账号是否唯一
     * @param email
     * @return
     */
    private boolean checkUnique(String email) {


        return false;
    }
}




