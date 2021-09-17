package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


/**
 * @author ZJWzxy
 * @date 2021/04/11
 */
@Service(version = "1.0.0")
public class RecommendUserApiImpl implements RecommendUserApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询得分最高的用户
     * @param userId 推荐用户id
     * @return RecommendUser
     */
    @Override
    public RecommendUser queryWithMaxScore(Long userId) {
        //设置查询条件----查询得分最高的用户<----按照得分倒序排序
        Query query=Query.query(Criteria.where("toUserId").is(userId))
                .with(Sort.by(Sort.Order.desc("score"))).limit(1);
        //调用mongoTemplate进行查询
        return  this.mongoTemplate.findOne(query,RecommendUser.class);
    }

    /**
     * 查询推荐用户
     * @param userId 推荐用户id
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return
     */
    @Override
    public PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize) {

        //分页并且倒序参数
        PageRequest pageRequest=PageRequest.of(pageNum-1,pageSize,Sort.by(Sort.Order.desc("score")));
        //设置查询条件
        Query query=Query.query(Criteria.where("toUserId").is(userId))
                .with(pageRequest);
        //调用mongoTemplate进行查询
        List<RecommendUser> recommendUserList = this.mongoTemplate.find(query, RecommendUser.class);
        //暂时不提供数据总数
        return new PageInfo<>(0,pageNum,pageSize,recommendUserList);
    }


    /**
     * 查询缘分值
     * @param userId  佳人id
     * @param toUserId  用户id
     * @return 分数
     */
    @Override
    public double queryScore(Long userId, Long toUserId) {
        //设置查询条件
        Query query = Query.query(Criteria
                .where("toUserId").is(toUserId)
                .and("userId").is(userId));
        RecommendUser recommendUser = this.mongoTemplate.findOne(query, RecommendUser.class);
        if (null == recommendUser) {
            return 0;
        }
        return recommendUser.getScore();
    }
}
