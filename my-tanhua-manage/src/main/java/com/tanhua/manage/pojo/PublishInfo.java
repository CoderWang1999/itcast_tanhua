package com.tanhua.manage.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishInfo  extends BasePojo{

    /**
     * 动态编号
     */
    private String id;
    /**
     * 作者昵称
     */
    private String nickname;
    /**
     * 作者id
     */
    private Integer userId;
    /**
     * 作者头像
     */
    private String userLogo;

    /**
     * 发布日期
     */
    private  Integer createDate;

    /**
     * 正文
     */
    private String text;


    /**
     * 审核状态，1为待审核，2为自动审核通过，3为待人工审核，4为人工审核拒绝，5为人工审核通过，6为自动审核拒绝
     */
    private Integer state;

    /**
     * 置顶状态，1为未置顶，2为置顶
     */
    private Integer topStatel;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 举报数
     */
    private Integer reportCount;

    /**
     * 转发数
     */
    private Integer forwardingCount;



}
