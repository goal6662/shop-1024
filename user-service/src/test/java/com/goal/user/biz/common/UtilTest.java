package com.goal.user.biz.common;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.goal.domain.LoginUser;
import com.goal.utils.JwtUtil;
import org.junit.Test;

public class UtilTest {

    @Test
    public void jwtTest() {

        LoginUser loginUser = new LoginUser();

        loginUser.setId(125L);
        loginUser.setMail("kd@123.com");
        loginUser.setHeadImg("https://baidu.com");
        loginUser.setName("kd");

//        String token = JwtUtil.genJwtToken(loginUser);
//        System.out.println(token);

        String token = "shop_1024:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzaG9wXzEwMjQiLCJzdWIiO" +
                "iIxMjUiLCJoZWFkX2ltZyI6Imh0dHBzOi8vYmFpZHUuY29tIiwibmFtZSI6ImtkIiwibWFpbCI6ImtkQDEyMy5jb20iLCJpY" +
                "XQiOjE3MTcyNTExNzgsImV4cCI6MTcxNzg1NTk3OH0.83WLBPKYA6Q00eqO5Kdl-SIvHPVr9Pu-G-D3_facw_g";

        DecodedJWT decodedJWT = JwtUtil.parseJwtToken(token);
        System.out.println(decodedJWT.getSubject());

    }

}
