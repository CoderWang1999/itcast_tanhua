package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo {

    private Long id;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 年龄
     */
    private String age;
    /**
     * 性别 man woman
     */
    private String gender;
    /**
     * 城市
     */
    private String city;
    /**
     * 学历 枚举: 本科,硕士,双硕,博士,双博
     */
    private String education;
    /**
     * 月收入 枚举: 5k,8K,15K,35K,55K,80K,100K
     */
    private String income;
    /**
     * 行业 枚举: IT行业,服务行业,公务员
     */
    private String profession;
    /**
     * 婚姻状态（0未婚，1已婚）
     */
    private Integer marriage;

}