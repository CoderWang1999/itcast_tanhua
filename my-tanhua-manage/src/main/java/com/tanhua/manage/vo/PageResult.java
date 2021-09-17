package com.tanhua.manage.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author ZJWzxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * 总记录数
     */
    private Integer counts;

    /**
     * 页大小
     */
    private Integer pagesize;
    /**
     * 页码
     */
    private Integer page;

    /**
     * 页数
     */
    private Integer pages;

    /**
     * 用户信息列表
     */
    private List<T> items= Collections.emptyList();

    /**
     * 状态合计
     */
    private List<T> totals=Collections.emptyList();
}
