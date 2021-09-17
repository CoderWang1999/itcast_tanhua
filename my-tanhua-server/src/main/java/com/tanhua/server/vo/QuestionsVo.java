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
public class QuestionsVo {
    /**
     * 试题编号
     */
    private String id;
    /**
     * 题目
     */
    private String question;
    /**
     * 选项集合
     */
    private List<?> options= Collections.EMPTY_LIST;

}
