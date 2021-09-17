package com.tanhua.server.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.RecommendUserApi;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.vo.TodayBest;
import org.springframework.stereotype.Service;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 * 负责与dubbo进行交互
 */
@Service
public class RecommendUserService {

    @Reference(version = "1.0.0")
    private RecommendUserApi recommendUserApi;

    /**
     * 查询推荐的用户---今日佳人  小明要查询小红(佳人)
     * @param userId  小明的id
     * @return 小红
     */
    public TodayBest queryTodayBest(Long userId) {
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        //判断
        if (null==recommendUser){
            //没有查询到
            return null;
        }
        //查询成功
        TodayBest todayBest=new TodayBest();
        //添加信息
        todayBest.setId(recommendUser.getUserId());
        //对分数进行取整操作,缘分值
        double  score=Math.floor(recommendUser.getScore());
        todayBest.setFateValue(Double.valueOf(score).longValue());
        //返回佳人
        return todayBest;
    }

    /**
     * 进行分页操作
     * @param userId 用户id
     * @param page 当前页码
     * @param pagesize 每页大小
     * @return
     */
    public PageInfo<RecommendUser> queryRecommendUserList(Long userId, Integer page, Integer pagesize) {
        return this.recommendUserApi.queryPageInfo(userId,page,pagesize);
    }


    /**
     * 查询缘分值
     * @param userId  佳人id
     * @param toUserId  用户id
     * @return 缘分分数
     */
    public double queryScore(Long userId, Long toUserId) {
        return this.recommendUserApi.queryScore(userId,toUserId);
    }
}
