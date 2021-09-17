package com.tanhua.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoulSimilarYou {

    /**
     * 用户编号
     */
    private Integer id;

    /**
     * 用户头像
     */
    private String avatar;
}
