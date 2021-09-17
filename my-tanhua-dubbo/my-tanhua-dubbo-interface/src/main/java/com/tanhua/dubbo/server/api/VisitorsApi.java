package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Visitors;

import java.util.List;

public interface VisitorsApi {

    /**
     * 保存来访记录
     *
     * @param visitors 访客
     * @return
     */
    String saveVisitor(Visitors visitors);

    /**
     * 按照时间倒序排序，查询最近的访客信息
     *
     * @param userId
     * @param num
     * @return
     */
    List<Visitors> topVisitor(Long userId, Integer num);

    /**
     * 按照时间倒序排序，查询最近的访客信息
     *
     * @param userId
     * @param date
     * @return
     */
    List<Visitors> topVisitor(Long userId, Long date);

    /**
     * 按照时间倒序排序，查询最近的访客信息
     *
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 访客信息
     */
    List<Visitors> topVisitor(Long userId, Integer page, Integer pageSize);
}
