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
public class PaperListVo {
    /**
     * 问卷id
     */
    private String  id;
    /**
     * 问卷名称
     */
    private String name;
    /**
     * 封面
     */
    private String cover;
    /**
     * 级别
     */
    private String level;
    /**
     * 星级
     */
    private Integer star;
    /**
     * 问题集合
     */
    private List<?> questions= Collections.EMPTY_LIST;
    /**
     * 是否锁住
     */
    private Integer isLock;
    /**
     * 最新报告id
     */
    private String reportId;
}
