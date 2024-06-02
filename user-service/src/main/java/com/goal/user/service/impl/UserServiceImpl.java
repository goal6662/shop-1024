package com.goal.user.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.domain.LoginUser;
import com.goal.domain.RefreshableToken;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.SendCodeEnum;
import com.goal.user.common.RedisConstants;
import com.goal.user.domain.User;
import com.goal.user.domain.dto.UserLoginDTO;
import com.goal.user.domain.dto.UserRegisterDTO;
import com.goal.user.domain.vo.UserVO;
import com.goal.user.mapper.UserMapper;
import com.goal.user.service.NotifyService;
import com.goal.user.service.UserService;
import com.goal.utils.CommonUtil;
import com.goal.utils.JwtUtil;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private StringRedisTemplate redisTemplate;

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

        String email = registerDTO.getMail();
        if (StringUtils.isBlank(email)
                || !notifyService.checkCode(SendCodeEnum.USER_REGISTER, email, registerDTO.getCode())) {
            return Result.fail(BizCodeEnum.CODE_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);

        user.setCreateTime(new Date());
        user.setSlogan("这个人什么也没有留下...");
        // 账号唯一性检查
        boolean isUnique = checkUnique(registerDTO.getMail());
        if (!isUnique) {
            return Result.fail(BizCodeEnum.ACCOUNT_REPEAT);
        }

        // 设置密码
        user.setSecret("$1$" + CommonUtil.getStringNumRandom(8));
        String cryptPwd = Md5Crypt.md5Crypt(user.getPwd().getBytes(), user.getSecret());
        user.setPwd(cryptPwd);

        if (this.save(user)) {
            log.info("用户注册成功：{}", user);

            // TODO: 2024/5/31 发放新用户福利
            userRegisterInitTask(user);
            return Result.success();
        }

        return Result.fail(BizCodeEnum.OPS_ERROR);
    }

    @Override
    public Result<String> login(UserLoginDTO loginDTO) {
        if (StringUtils.isAnyBlank(loginDTO.getMail(), loginDTO.getPwd())) {
            return Result.fail(BizCodeEnum.REQ_PARAM_ILLEGAL);
        }

        // 1. 根据邮箱查找用户
        User user = userMapper.getUserByMail(loginDTO.getMail());
        if (user == null) {
            return Result.fail(BizCodeEnum.ACCOUNT_UNREGISTER);
        }

        // 2. 获取密钥，进行加密，匹配密文
        // 使用用户密钥加密请求密码
        String cryptPwd = Md5Crypt.md5Crypt(loginDTO.getPwd().getBytes(), user.getSecret());

        // 3. 判断账号密码是否一致
        if (cryptPwd.equals(user.getPwd())) {
            // 登录成功，生成token
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(user, loginUser);

            return getTokenResult(loginUser);
//            return getRefreshResult(loginUser);
        }

        return Result.fail(BizCodeEnum.ACCOUNT_PWD_ERROR);
    }

    @Override
    public Result<RefreshableToken> refreshToken(RefreshableToken token) {
        // 1. 判断缓存是否存在
        if (Boolean.TRUE.equals(redisTemplate.hasKey(token.getRefreshToken()))) {
            // 存在
            DecodedJWT decoded = JwtUtil.parseJwtToken(token.getRefreshToken());
            LoginUser loginUser = LoginUser.builder()
                    .id(Long.valueOf(decoded.getSubject()))
                    .headImg(decoded.getClaim("head_img").asString())
                    .name(decoded.getClaim("name").asString())
                    .mail(decoded.getClaim("mail").asString())
                    .build();
            Result result = getRefreshResult(loginUser);

            // 删除原有缓存
            redisTemplate.delete(token.getRefreshToken());

            return result;
        }
        return Result.fail(BizCodeEnum.ACCOUNT_LOGIN_EXPIRED);
    }

    @Override
    public Result<UserVO> findUserDetail() {
        Long userId = UserContext.getUser().getId();

        User user = userMapper.selectById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return Result.success(userVO);
    }

    /**
     * 发放新用户福利
     * @param user 新用户
     */
    private void userRegisterInitTask(User user) {
    }

    /**
     * 检查账号是否唯一
     *  高并发下可能出现账号重复
     *  mail 字段的 unique 属性是必须的
     * @param email
     * @return
     */
    private boolean checkUnique(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getMail, email);

        List<User> users = userMapper.selectList(queryWrapper);

        return users.isEmpty();
    }


    /**
     * 普通方案
     */
    private Result<String> getTokenResult(LoginUser loginUser) {
        String token = JwtUtil.genJwtToken(loginUser);
        return Result.success(token);
    }

    /**
     * 自动刷新方案
     * @param loginUser
     * @return
     */
    private Result getRefreshResult(LoginUser loginUser) {
        String accessToken = JwtUtil.genJwtToken(loginUser);
        String refreshToken = JwtUtil.genJwtToken(loginUser, RedisConstants.REFRESH_TOKEN_TTL);

        RefreshableToken token = new RefreshableToken(accessToken, refreshToken);

        if (StringUtils.isBlank(token.getExpireTime())) {
            return Result.fail(BizCodeEnum.OPS_ERROR);
        }

        // 缓存 refreshToken
        redisTemplate.opsForValue().set(refreshToken, "1",
                RedisConstants.REFRESH_TOKEN_TTL, TimeUnit.MILLISECONDS);

        return Result.success(token);

    }
}




