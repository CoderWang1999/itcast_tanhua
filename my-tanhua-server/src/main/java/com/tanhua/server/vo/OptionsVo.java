package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionsVo {
    /**
     * 主键id
     */
    private String id;
    /**
     * 选项内容
     */
    private String option;
}
