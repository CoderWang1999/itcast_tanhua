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
public class SoulDimensions {

    /**
     * 维度项（外向，判断，抽象，理性）
     */
    private String key;

    /**
     * 维度值
     */
    private String value;

}
