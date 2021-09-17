package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author ZJWzxy
 * @date 2021/04/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommend_user")
public class RecommendUser implements java.io.Serializable{

    private static final long serialVersionUID = -4296017160071130962L;
    /**
     * 主键id
     */
    @Id
    private ObjectId id;
    /**
     * 推荐用户id
     */
    @Indexed
    private Long userId;
    /**
     * 用户id
     */
    private Long toUserId;
    /**
     * 推荐得分
     */
    @Indexed
    private Double score;
    /**
     * 日期
     */
    private String date;
}
