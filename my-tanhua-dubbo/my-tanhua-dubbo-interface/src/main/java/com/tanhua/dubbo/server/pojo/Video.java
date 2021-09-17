package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author ZJWzxy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video")
public class Video implements java.io.Serializable {

    private static final long serialVersionUID = -3136732836884933873L;

    /**
     * 主键id
     */
    private ObjectId id;
    /**
     * 发布人id
     */
    private Long userId;
    /**
     * 小视频自增id
     */
    private Long vid;
    /**
     * 文字
     */
    private String text;
    /**
     * 视频封面文件
     */
    private String picUrl;
    /**
     * 视频文件
     */
    private String videoUrl;
    /**
     * 创建时间
     */
    private Long created;
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
     * 纬度
     */
    private String latitude;
    /**
     * 位置名称
     */
    private String locationName;
}
