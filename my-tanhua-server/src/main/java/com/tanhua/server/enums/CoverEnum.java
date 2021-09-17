package com.tanhua.server.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author ZJWzxy
 * 鉴定图片
 */
@SuppressWarnings("all")
public enum CoverEnum implements IEnum<Integer>{

    MAOTTOUYING(1,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/owl.png"),
    BAITU(2,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/rabbit.png"),
    HULI(3,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/fox.png"),
    SHIZI(4,"https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/lion.png");

    private int value;
    private String desc;

    CoverEnum(int value, String desc) {
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
