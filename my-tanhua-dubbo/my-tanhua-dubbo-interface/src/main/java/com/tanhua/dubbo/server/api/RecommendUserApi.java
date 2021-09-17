package com.tanhua.dubbo.server.api;


import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.springframework.stereotype.Repository;

/**
 * @author ZJWzxy
 * @date 2021/04/11
 */
public interface RecommendUserApi {

    /**
     * 查询一位得分最高的推荐用户
     *
     * @param userId 推荐用户id
     * @return
     */
    RecommendUser queryWithMaxScore(Long userId);

    /**
     * 按照得分倒序
     *
     * @param userId 推荐用户id
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return
     */
    PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询缘分值
     * @param userId  佳人id
     * @param toUserId  用户id
     * @return 缘分分数
     */
    double queryScore(Long userId, Long toUserId);
}
