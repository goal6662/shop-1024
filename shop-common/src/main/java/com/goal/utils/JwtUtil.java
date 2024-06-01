package com.goal.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.goal.domain.LoginUser;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    /**
     * 过期时间
     */
    private static final long EXPIRE = 1000L * 60 * 60 * 24 * 7;    // 7 day

    /**
     * 加密密钥
     */
    private static final String SECRET = "LinKirby";

    private static final String TOKEN_PREFIX = "shop_1024:";

    /**
     * 签发人
     */
    private static final String ISS_USR = "shop_1024";


    /**
     * 根据用户信息生成token
     *
     * @param loginUser
     * @return
     */
    public static String genJwtToken(LoginUser loginUser) {
        return genJwtToken(loginUser, EXPIRE);
    }

    /**
     * 根据用户信息生成token
     *
     * @param loginUser
     * @return
     */
    public static String genJwtToken(LoginUser loginUser, long expire) {
        if (loginUser == null) {
            throw new NullPointerException("LoginUser对象为空");
        }

        Long userId = loginUser.getId();
        String token = JWT.create()
                .withIssuer(ISS_USR)    // 签发人
                .withSubject(String.valueOf(userId))   // 受用人
                .withClaim("head_img", loginUser.getHeadImg())  // 自定义信息
                .withClaim("name", loginUser.getName())
                .withClaim("mail", loginUser.getMail())
                .withIssuedAt(new Date())   // 签发时间
                .withExpiresAt(new Date(System.currentTimeMillis() + expire))    // 过期时间
                .sign(Algorithm.HMAC256(SECRET));

        return TOKEN_PREFIX + token;
    }


    /**
     * 校验 jwt 获取负载信息
     *
     * @param token
     * @return
     */
    public static Map<String, Claim> checkJWT(String token) {

        DecodedJWT decodedJWT = parseJwtToken(token);

        if (decodedJWT != null) {
            return decodedJWT.getClaims();
        }

        return null;
    }

    /**
     * 解析 jwt
     *
     * @param token
     * @return 解析结果
     */
    public static DecodedJWT parseJwtToken(String token) {
        String subject = null;
        try {
            // 去除类型信息
            token = token.substring(token.indexOf(":") + 1);

            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                    .withIssuer(ISS_USR)
                    .build();
            subject = JWT.decode(token).getSubject();
            return verifier.verify(token);
        } catch (Exception e) {
            log.warn("jwt解析失败，相关用户: {}", subject);
            return null;
        }
    }

    /**
     * 获取 jwt 有效期
     * @param token
     * @return
     */
    public static String getExpireTime(String token) {
        try {
            // 去除类型信息
            token = token.substring(token.indexOf(":") + 1);

            return String.valueOf(JWT.decode(token).getExpiresAt().getTime());
        } catch (Exception e) {
            log.error("获取jwt有效期失败");
            return null;
        }
    }

}
