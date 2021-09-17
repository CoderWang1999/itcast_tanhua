package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 今日佳人
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayBest {

    private Long id;
    private String avatar;
    private String nickname;
    /**
     * 性别 man woman
     */
    private String gender;
    private Integer age;
    private String[] tags;
    /**
     * 缘分值
     */
    private Long fateValue;

}
