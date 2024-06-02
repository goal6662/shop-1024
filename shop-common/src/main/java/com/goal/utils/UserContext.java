package com.goal.utils;

import com.goal.domain.LoginUser;

public class UserContext {

    private static final ThreadLocal<LoginUser> user = new ThreadLocal<>();

    public static LoginUser getUser() {
        return user.get();
    }

    public static void setUser(LoginUser loginUser) {
        UserContext.user.set(loginUser);
    }

    public static void clearUser() {
        UserContext.user.remove();
    }

}
