package com.tanhua.manage.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalsVo {

    /**
     * 状态标题
     */
    private String title;

    /**
     * 状态代码
     */
    private String code;

    /**
     * 状态数量
     */
    private Integer value;

}
