package com.tanhua.server.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author ZJWzxy
 */

public enum SexEnum implements IEnum<Integer> {

    /**
     * 根据数据库中1,2,3的值来判断性别
     */
    MAN(1,"男"),
    WOMAN(2,"女"),
    UNKNOWN(3,"未知");

    private int value;
    private String desc;

    SexEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
