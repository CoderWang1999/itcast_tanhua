package com.tanhua.manage.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum SexEnum implements IEnum<Integer> {

    MAN(1, "男"),
    WOMAN(2, "女"),
    UNKNOWN(3, "未知");

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


    public static String getSexByValue(int code) {
        String sex = "未知";
        for (SexEnum sexEnum : values()) {
            if (sexEnum.value == code) {
                sex = sexEnum.desc;
            }
        }
        return sex;
    }

}
