package com.tanhua.manage.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Summary {

    private Integer cumulativeUsers;//累计用户
    private Integer activePassMonth;//过去30天活跃用户
    private Integer activePassWeek;//过去7天活跃用户
    private Integer newUsersToday;//今日新增用户
    private Integer loginTimesToday;//今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
    private Integer activeUsersToday;//今日登录次数
    private Integer useTimePassWeek;//今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
    private Integer activeUsersYesterday;//今日活跃用户
    private Integer newUsersTodayRate;//今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
    private Integer loginTimesTodayRate;//过去7天平均日使用时长，单位秒
    private Integer activeUsersTodayRate;//昨日活跃用户
    private Integer activeUsersYesterdayRate;//昨日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
}
