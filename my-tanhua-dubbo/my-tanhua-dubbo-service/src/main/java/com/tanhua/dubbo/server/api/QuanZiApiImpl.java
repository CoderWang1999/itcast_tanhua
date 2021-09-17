package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.dubbo.server.pojo.*;
import com.tanhua.dubbo.server.service.IdService;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 */
@Service(version = "1.0.0")
public class QuanZiApiImpl implements QuanZiApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    /**
     * 发布动态
     *
     * @param publish 动态
     * @return 是否成功
     */
    @Override
    public String savePublish(Publish publish) {
        //首先校验发布的动态是否有合法id
        if (null == publish.getUserId()) {
            return null;
        }
        try {
            //1.将动态发布到发布表
            //设置id----->为了让相册表和时间线表有具体指向--->相册表和时间线表中的publishId就会指向此id
            publish.setId(ObjectId.get());
            //设置谁可以看 1代表公开
            publish.setSeeType(1);
            //增加自增长pid
            publish.setPid(this.idService.createId("publish", publish.getId().toHexString()));
            //保存发布
            this.mongoTemplate.save(publish);
            //2.将动态保存到相册表
            Album album = new Album();
            //主键id---无特殊用处
            album.setId(ObjectId.get());
            //设置动态id,其值为发布表中的主键id
            album.setPublishId(publish.getId());
            //设置发布时间
            album.setCreated(System.currentTimeMillis());
            //保存---->每个人都有自己的相册表,所以要加唯一标识
            this.mongoTemplate.save(album, "quanzi_album_" + publish.getUserId());
            //3.将动态保存到时间线表
            //设置查询条件---首先查询出自己的所有好友---->好友关系表Users
            Query query = Query.query(Criteria.where("userId").is(publish.getUserId()));
            List<Users> users = this.mongoTemplate.find(query, Users.class);
            //向好友的时间线表中插入自己的动态------好友的时间线表也是有唯一标识的
            for (Users user : users) {
                TimeLine timeLine = new TimeLine();
                //主键id,无特殊意义
                timeLine.setId(ObjectId.get());
                //设置发布者的id
                timeLine.setUserId(publish.getUserId());
                //设置发布id
                timeLine.setPublishId(publish.getId());
                //设置发布时间
                timeLine.setDate(System.currentTimeMillis());
                //保存
                this.mongoTemplate.save(timeLine, "quanzi_time_line_" + user.getFriendId());
                return publish.getId().toHexString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 查询好友动态
     *
     * @param userId   用户自己id
     * @param page     页码
     * @param pageSize 每页显示条数
     * @return 分页集合
     */
    @Override
    public PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize) {
        //设置查询条件---->按照时间排序
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("date")));
        //查询
        Query query = new Query().with(pageRequest);
        //判断是好友列表还是推荐列表
        String collectionName = "quanzi_time_line_" + userId;
        if (null == userId) {
            collectionName = "quanzi_time_line_recommend";
        }
        //根据用户自己id得到自己的时间线表
        List<TimeLine> timeLineList = this.mongoTemplate.find(query, TimeLine.class, collectionName);
        //创建一个集合保存所有的动态id
        List<ObjectId> publishIds = new ArrayList<>();
        for (TimeLine timeLine : timeLineList) {
            publishIds.add(timeLine.getPublishId());
        }
        //根据动态id查询动态的信息
        Query queryPublish = Query.query(Criteria.where("id").in(publishIds)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(queryPublish, Publish.class);
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(publishList);
        pageInfo.setTotal(0);
        return pageInfo;
    }

    /**
     * 查询所有动态
     * @param page  页码
     * @param pageSize 页大小
     * @return 动态集合
     */
    @Override
    public PageInfo<Publish> queryAllPublish(Integer page, Integer pageSize) {
        //设置查询条件---->按照时间排序
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        //查询
        Query query = new Query().with(pageRequest);
        List<Publish> publishList = this.mongoTemplate.find(query, Publish.class);
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(publishList);
        return pageInfo;
    }

    /**
     * 查询动态数量
     * @return 数量
     */
    @Override
    public Long queryCountPublish() {
        Query query=new Query();
        return  this.mongoTemplate.count(query,Publish.class);
    }

    /**
     * 查询待审核,已通过,驳回的数量
     * @param state 审核状态
     * @return 数量
     */
    @Override
    public Long queryCountWait(Integer state) {
        Query query=Query.query(Criteria.where("state").is(state));
        return this.mongoTemplate.count(query,Publish.class);
    }

    /**
     * 点赞
     *
     * @param userId    用户id
     * @param publishId 发布id
     * @return true or false
     */
    @Override
    public boolean saveLikeComment(Long userId, String publishId) {
        //判断是否已经点赞----设置条件---如果一个动态的评论人id,动态id,类型为点赞全部符合的话,说明已经存在
        Query query = Query.query(Criteria
                .where("publishId").is(new ObjectId(publishId))
                .and("userId").is(userId)
                .and("commentType").is(1));
        long count = this.mongoTemplate.count(query, Comment.class);
        if (count > 0) {
            //已经点赞
            return false;
        }
        //没有点赞
        return this.saveComment(userId, publishId, 1, null);

    }

    /**
     * 取消点赞喜欢评论等
     *
     * @param userId      评论id
     * @param publishId   发布id
     * @param commentType 评论类型
     * @return @return true or false
     */
    @Override
    public boolean removeComment(Long userId, String publishId, Integer commentType) {
        Query query = Query.query(Criteria
                .where("publishId").is(new ObjectId(publishId))
                .and("userId").is(userId)
                .and("commentType").is(commentType));
        DeleteResult remove = this.mongoTemplate.remove(query, Comment.class);
        //判断是否删除成功 >0代表删除成功
        return remove.getDeletedCount() > 0;
    }

    /**
     * 喜欢
     *
     * @param userId    评论人id---用户id
     * @param publishId 动态id
     * @return true or false
     */
    @Override
    public boolean saveLoveComment(Long userId, String publishId) {
        //判断是否已经喜欢----设置条件---如果一个动态的评论人id,动态id,类型为喜欢--3全部符合的话,说明已经存在
        Query query = Query.query(Criteria
                .where("publishId").is(new ObjectId(publishId))
                .and("userId").is(userId)
                .and("commentType").is(3));
        //去MonGoDB中查询
        long count = this.mongoTemplate.count(query, Comment.class);
        //查到
        if (count > 0) {
            //已经喜欢
            return false;
        }
        //没有喜欢
        return this.saveComment(userId, publishId, 3, null);
    }

    /**
     * 保存评论到MonGoDB
     *
     * @param userId    评论人id
     * @param publishId 发布id
     * @param type      评论类型
     * @param content   评论内容
     * @return true or false
     */
    @Override
    public boolean saveComment(Long userId, String publishId, Integer type, String content) {
        try {
            Comment comment = new Comment();
            //填充信息
            //主键id
            comment.setId(ObjectId.get());
            //设置评论人信息
            comment.setUserId(userId);
            //评论内容
            comment.setContent(content);
            //动态id
            comment.setPublishId(new ObjectId(publishId));
            //发布类型
            comment.setCommentType(type);
            //发表时间
            comment.setCreated(System.currentTimeMillis());
            //设置发布人的id
            Publish publish = this.mongoTemplate.findById(comment.getPublishId(), Publish.class);
            if (null != publish) {
                comment.setPublishUser(publish.getUserId());
            } else {
                Video video = this.mongoTemplate.findById(comment.getPublishId(), Video.class);
                if (null != video) {
                    comment.setPublishUser(video.getUserId());
                }
            }
            this.mongoTemplate.save(comment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询数量
     *
     * @param publishId 动态id
     * @param type      类型
     * @return 数量
     */
    @Override
    public Long queryCommentCount(String publishId, Integer type) {
        Query query = Query.query(Criteria.where("publishId").is(publishId).and("commentType").is(type));
        return this.mongoTemplate.count(query, Comment.class);
    }

    /**
     * 查询单条动态
     *
     * @param id 动态id
     * @return 所查询的动态
     */
    @Override
    public Publish queryPublishById(String id) {
        return this.mongoTemplate.findById(new ObjectId(id), Publish.class);
    }

    /**
     * 查询评论
     *
     * @param publishId 动态id
     * @param page      页码
     * @param pageSize  页大小
     * @return 评论内容
     */
    @Override
    public PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.asc("created")));

        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(publishId))
                .and("commentType").is(2)).with(pageRequest);

        List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);

        PageInfo<Comment> pageInfo = new PageInfo<>();
        pageInfo.setTotal(0);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPageNum(page);
        pageInfo.setRecords(commentList);

        return pageInfo;

    }


    /**
     * 查询用户的评论数据
     *
     * @param userId   用户id
     * @param type     类型
     * @param page     页码
     * @param pageSize 页大小
     * @return 分页集合
     */
    @Override
    public PageInfo<Comment> queryCommentListByUser(Long userId, Integer type, Integer page, Integer pageSize) {
        //设置分页条件
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        //设置查询条件
        Query query = new Query(Criteria.where("publishUser").is(userId).and("commentType").is(type)).with(pageRequest);
        //查询
        List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);
        PageInfo<Comment> pageInfo = new PageInfo<>(0, page, pageSize, commentList);
        return pageInfo;
    }

    /**
     * 通过redis查询
     * @param pidList id
     * @return 集合
     */
    @Override
    public List<Publish> queryPublishByPids(List<Long> pidList) {
        Query query = Query.query(Criteria.where("pid").in(pidList));
        return this.mongoTemplate.find(query, Publish.class);
    }

    /**
     * 查询相册表
     * @param userId 佳人id
     * @param page 页码
     * @param pageSize 页大小
     * @return 佳人信息
     */
    @Override
    public PageInfo<Publish> queryAlbumList(Long userId, Integer page, Integer pageSize) {

        //设置分页信息
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        //设置查询条件---->查询当前人的相册
        Query query = new Query().with(pageRequest);
        List<Album> albumList = this.mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(0);
        //判断是否查询到
        if (CollectionUtils.isEmpty(albumList)){
            return pageInfo;
        }
        //创建一个集合用来保存所有的动态id
        List<ObjectId> publishIds = new ArrayList<>();
        for (Album album : albumList) {
            publishIds.add(album.getPublishId());
        }
        //查询发布信息
        Query queryPublish = Query.query(Criteria.where("id").in(publishIds)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(queryPublish, Publish.class);

        pageInfo.setRecords(publishList);

        return pageInfo;

    }
}
