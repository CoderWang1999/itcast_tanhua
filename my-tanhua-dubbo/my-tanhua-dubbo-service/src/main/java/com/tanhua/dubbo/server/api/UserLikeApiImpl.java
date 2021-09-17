package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.dubbo.server.pojo.UserLike;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service(version = "1.0.0")
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存喜欢
     * @param userId 自己id
     * @param likeUserId 对方id
     * @return String
     */
    @Override
    public String saveUserLike(Long userId, Long likeUserId) {
        Query query = Query.query(Criteria
                .where("userId")
                .is(userId)
                .and("likeUserId").is(likeUserId));
        //判断是否已经喜欢
        if (this.mongoTemplate.count(query, UserLike.class) > 0) {
            //已经喜欢
            return null;
        }

        UserLike userLike = new UserLike();
        userLike.setId(ObjectId.get());
        userLike.setCreated(System.currentTimeMillis());
        userLike.setUserId(userId);
        userLike.setLikeUserId(likeUserId);

        this.mongoTemplate.save(userLike);
        return userLike.getId().toHexString();
    }

    /**
     * 相互喜欢
     * @param userId 自己id
     * @param likeUserId 对方id
     * @return true or false
     */
    @Override
    public Boolean isMutualLike(Long userId, Long likeUserId) {
        Criteria criteria1 = Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId);
        Criteria criteria2 = Criteria.where("userId").is(likeUserId).and("likeUserId").is(userId);
        Criteria criteria = new Criteria().orOperator(criteria1, criteria2);
        return this.mongoTemplate.count(Query.query(criteria), UserLike.class) == 2;
    }

    /**
     * 删除喜欢
     * @param userId 自己id
     * @param likeUserId 对方id
     * @return true or false
     */
    @Override
    public Boolean deleteUserLike(Long userId, Long likeUserId) {
        Query query = Query.query(Criteria
                .where("userId")
                .is(userId)
                .and("likeUserId").is(likeUserId));
        DeleteResult deleteResult = this.mongoTemplate.remove(query, UserLike.class);
        return deleteResult.getDeletedCount() == 1;
    }

    /**
     * 相互喜欢的数量
     * @param userId  用户id
     * @return 数量
     */
    @Override
    public Long queryEachLikeCount(Long userId) {
        // 我喜欢的列表
        List<UserLike> userLikeList = this.mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);

        // 获取到所有我喜欢的列表的用户id
        List<Long> likeUserIdList = new ArrayList<>();
        for (UserLike userLike : userLikeList) {
            likeUserIdList.add(userLike.getLikeUserId());
        }

        Query query = Query.query(Criteria.where("userId").in(likeUserIdList).and("likeUserId").is(userId));
        return this.mongoTemplate.count(query, UserLike.class);
    }

    /**
     * 喜欢数
     * @param userId  用户id
     * @return 数量
     */
    @Override
    public Long queryLikeCount(Long userId) {
        return this.mongoTemplate.count(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
    }

    /**
     * 粉丝数
     * @param userId  用户id
     * @return 数量
     */
    @Override
    public Long queryFanCount(Long userId) {
        return this.mongoTemplate.count(Query.query(Criteria.where("likeUserId").is(userId)), UserLike.class);
    }

    /**
     * 互相喜欢列表
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    @Override
    public List<UserLike> queryEachLikeList(Long userId, Integer page, Integer pageSize) {
        // 我喜欢的列表
        List<UserLike> userLikeList = this.mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);

        // 获取到所有我喜欢的列表的用户id
        List<Long> likeUserIdList = new ArrayList<>();
        for (UserLike userLike : userLikeList) {
            likeUserIdList.add(userLike.getLikeUserId());
        }

        Query query = Query.query(Criteria.where("userId").in(likeUserIdList).and("likeUserId").is(userId));
        return this.queryList(query, page, pageSize);

    }

    /**
     * 喜欢列表
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    @Override
    public List<UserLike> queryLikeList(Long userId, Integer page, Integer pageSize) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return this.queryList(query, page, pageSize);
    }

    /**
     * 粉丝列表
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    @Override
    public List<UserLike> queryFanList(Long userId, Integer page, Integer pageSize) {
        return this.queryList(Query.query(Criteria.where("likeUserId").is(userId)), page, pageSize);
    }

    /**
     * 列表公共方法
     * @param query 查询条件
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    private List<UserLike> queryList(Query query, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        query.with(pageRequest);
        List<UserLike> userLikeList = this.mongoTemplate.find(query, UserLike.class);
        return userLikeList;
    }
}
