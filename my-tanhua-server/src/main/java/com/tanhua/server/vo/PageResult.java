package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {
    /**
     * 总记录数
     */
    private Integer counts = 0;
    /**
     * 页大小
     */
    private Integer pagesize = 0;
    /**
     * 总页数
     */
    private Integer pages = 0;
    /**
     * 当前页码
     */
    private Integer page = 0;
    /**
     * 列表
     */
    private List<?> items = Collections.emptyList();

}