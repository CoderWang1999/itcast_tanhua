package com.tanhua.manage.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.List;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishVo {
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
    private  Long createDate;

    /**
     * 正文
     */
    private String text;

    /**
     * 图片列表,小视频url
     */
    private List<String> medias;

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
