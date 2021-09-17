package com.tanhua.recommend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/**
 * @author ZJWzxy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendQuanZi {

    private ObjectId id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 动态id，需要转化为Long类型
     */
    private Long publishId;
    /**
     * 得分
     */
    private Double score;
    /**
     * 时间戳
     */
    private Long date;
}
