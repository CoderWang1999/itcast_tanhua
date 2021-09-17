package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 * 发布表，动态内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_publish")
public class Publish implements java.io.Serializable {

    private static final long serialVersionUID = 8732308321082804771L;
    /**
     * 主键id---->作为其他表的指向
     */
    private ObjectId id;

    /**
     * Long类型的id，用于推荐引擎使用
     */
    private Long pid;
    /**
     * 发布者id
     */
    private Long userId;
    /**
     * 文字
     */
    private String text;
    /**
     * 媒体数据，图片或小视频 url
     */
    private List<String> medias;
    /**
     * 谁可以看，1-公开，2-私密，3-部分可见，4-不给谁看
     */
    private Integer seeType;
    /**
     * 部分可见的列表
     */
    private List<Long> seeList;
    /**
     * 不给谁看的列表
     */
    private List<Long> notSeeList;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;
    /**
     *位置名称
     */
    private String locationName;
    /**
     * 发布时间
     */
    private Long created;

    /**
     * 审核状态，1为待审核，2为自动审核通过，3为待人工审核，4为人工审核拒绝，5为人工审核通过，6为自动审核拒绝
     */
    private Integer state;
}