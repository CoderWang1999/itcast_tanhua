package com.tanhua.manage.util;


import com.tanhua.manage.pojo.User;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 */
public class UserThreadLocal {

    private static final ThreadLocal<User> LOCAL = new ThreadLocal<User>();

    private UserThreadLocal() {

    }

    public static void set(User user) {
        LOCAL.set(user);
    }

    public static User get() {
        return LOCAL.get();
    }

}
