package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author ZJWzxy
 */
@Service(version = "1.0.0")
public class UsersApiImpl implements UsersApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存好友关系
     *
     * @param users 好友
     * @return 字符串
     */
    @Override
    public String saveUsers(Users users) {

        if (users.getFriendId() == null || users.getUserId() == null) {
            return null;
        }

        // 检测是否该好友关系是否存在
        Query query = Query.query(Criteria.where("userId").is(users.getUserId()).and("friendId").is(users.getFriendId()));
        Users oldUsers = this.mongoTemplate.findOne(query, Users.class);
        if (null != oldUsers) {
            //好友关系存在
            return null;
        }

        users.setId(ObjectId.get());
        users.setDate(System.currentTimeMillis());

        this.mongoTemplate.save(users);
        return users.getId().toHexString();
    }


    /**
     * 查询好友列表
     * @param userId 用户id
     * @return 集合
     */
    @Override
    public List<Users> queryAllUsersList(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.find(query, Users.class);
    }

    /**
     * 查询好友列表(分页)
     * @param userId 用户id
     * @param page  页码
     * @param pageSize  页大小
     * @return 分页集合
     */
    @Override
    public PageInfo<Users> queryUsersList(Long userId, Integer page, Integer pageSize) {
        //设置分页条件
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
       //设置查询条件
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageRequest);
        List<Users> usersList = this.mongoTemplate.find(query, Users.class);
        //填充信息
        PageInfo<Users> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(usersList);
        pageInfo.setTotal(0);
        return pageInfo;
    }
}
