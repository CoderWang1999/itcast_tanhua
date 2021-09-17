package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论
 * @author ZJWzxy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments {
    /**
     * 评论id
     */
    private String id;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论时间: 08:27
     */
    private String createDate;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 是否点赞（1是，0否）
     */
    private Integer hasLiked;

}