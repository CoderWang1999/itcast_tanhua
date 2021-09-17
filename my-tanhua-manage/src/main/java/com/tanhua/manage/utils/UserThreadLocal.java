package com.tanhua.manage.utils;

import com.tanhua.manage.pojo.Admin;

public class UserThreadLocal {

    private static final ThreadLocal<Admin> LOCAL = new ThreadLocal<>();

    private UserThreadLocal() {

    }

    public static void set(Admin admin) {
        LOCAL.set(admin);
    }

    public static Admin get() {
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}