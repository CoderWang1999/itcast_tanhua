package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Comments;
import com.tanhua.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author ZJWzxy
 */
@Service
public class CommentsService {

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String ,String> redisTemplate;
    /**
     * 查询评论列表
     * @param publishId 动态id
     * @param page 页码
     * @param pagesize 页大小
     * @return 分页后的结果
     */
    public PageResult queryCommentsList(String publishId, Integer page, Integer pagesize) {
        //校验
        User user = UserThreadLocal.get();
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPagesize(pagesize);
        //调用QuanZiApi中的方法进行查询
        PageInfo<Comment> pageInfo = this.quanZiApi.queryCommentList(publishId, page, pagesize);
        //拿到数据
        List<Comment> records = pageInfo.getRecords();
        if (CollectionUtils.isEmpty(records)){
            //为空
            return pageResult;
        }
        //如果不为空, 创建集合拿到所有评论人的id
        Set<Long> userIds=new HashSet<>();
        for (Comment record : records) {
            userIds.add(record.getUserId());
        }
        //创建集合来保存所有的评论
        List<Comments> commentList=new ArrayList<>();
        //查询所有评论人的信息--->设置查询条件
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("user_id",userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        for (Comment record : records) {
            //创建评论对象--添加评论信息
            Comments comments=new Comments();
            comments.setId(record.getId().toHexString());
            comments.setCreateDate(new DateTime(record.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
            comments.setContent(record.getContent());
            //添加评论人信息
            for (UserInfo userInfo : userInfoList) {
                if (record.getUserId().longValue()==userInfo.getUserId().longValue()){
                    //填充
                    comments.setAvatar(userInfo.getLogo());
                    comments.setNickname(userInfo.getNickName());
                    break;
                }
            }
            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + comments.getId();
            //是否点赞
            comments.setHasLiked(this.redisTemplate.hasKey(likeUserCommentKey) ? 1 : 0);

            String likeCommentKey = "QUANZI_COMMENT_LIKE_" + comments.getId();
            String value = this.redisTemplate.opsForValue().get(likeCommentKey);
            if(StringUtils.isNotEmpty(value)){
                //点赞数
                comments.setLikeCount(Integer.valueOf(value));
            }else{
                //点赞数
                comments.setLikeCount(0);
            }
            commentList.add(comments);
        }
        pageResult.setItems(commentList);
        return pageResult;
    }

    /**
     * 发表评论
     *
     * @param publishId 动态编号
     * @param content 评论内容
     * @return 状态信息
     */
    public Boolean saveComments(String publishId, String content) {
        //校验
        User user = UserThreadLocal.get();
        return this.quanZiApi.saveComment(user.getId(),publishId,2,content);
    }
}
