package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConclusionVo  {

    /**
     * 鉴定结果
     */
    private String conclusion;

    /**
     * 鉴定图片
     */
    private  String cover;

    /**
     * 维度
     */
    private List<?> dimensions= Collections.EMPTY_LIST;

    /**
     * 与你相似
     */
    private List<?> similarYou=Collections.EMPTY_LIST;

}
