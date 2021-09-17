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
public class VideoVo {

    /**
     * 主键id---无特殊意义
     */
    private String id;
    /**
     * 发布者id
     */
    private Long userId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 封面
     */
    private String cover;
    /**
     * 视频URL
     */
    private String videoUrl;
    /**
     * 签名
     */
    private String signature;
    /**
     * 点赞数量
     */
    private Integer likeCount;
    /**
     * 是否已赞（1是，0否）
     */
    private Integer hasLiked;
    /**
     * 是是否关注 （1是，0否）
     */
    private Integer hasFocus;
    /**
     * //评论数量
     */
    private Integer commentCount;
}
