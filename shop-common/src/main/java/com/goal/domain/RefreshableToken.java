package com.goal.domain;

import com.goal.utils.JwtUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshableToken {

    private String accessToken;

    private String refreshToken;

    private String expireTime;

    public RefreshableToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = JwtUtil.getExpireTime(accessToken);
    }

}
