package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.dubbo.server.vo.PageInfo;

import java.util.List;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 */
public interface QuanZiApi {

    /**
     * 发布动态
     *
     * @param publish 动态
     * @return true or false
     */
    String savePublish(Publish publish);

    /**
     * 查询好友动态
     *
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页后的结果
     */
    PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize);

    /**
     * 查询所有动态
     * @param page  页码
     * @param pageSize 页大小
     * @return
     */
    PageInfo<Publish> queryAllPublish( Integer page,Integer pageSize);

    /**
     * 统计动态条数
     * @return 数量
     */
    Long queryCountPublish();


    /**
     * @param state  审核状态
     * 查询待审核数量
     */
    Long queryCountWait(Integer state);


    /**
     * 点赞
     * @param userId 用户id
     * @param publishId 动态id
     * @return true or false
     */
    boolean saveLikeComment(Long userId, String publishId);

    /**
     * 取消点赞、喜欢等
     *
     * @param userId 用户id
     * @param publishId 动态id
     * @param commentType  类型----取消的是点赞还是喜欢
     * @return true or false
     */
    boolean removeComment(Long userId, String publishId, Integer commentType);

    /**
     * 喜欢
     *
     * @param userId  用户id
     * @param publishId 动态id
     * @return true or false
     */
    boolean saveLoveComment(Long userId, String publishId);

    /**
     * 保存评论
     *
     * @param userId 评论人id
     * @param publishId 动态id
     * @param type 类型
     * @param content 文字
     * @return true or false
     */
    boolean saveComment(Long userId, String publishId, Integer type, String content);

    /**
     * 查询评论数
     *
     * @param publishId 动态id
     * @param type 类型
     * @return 数量
     */
    Long queryCommentCount(String publishId, Integer type);


    /**
     * 根据id查询动态
     *
     * @param id 动态id
     * @return 动态
     */
    Publish queryPublishById(String id);

    /**
     * 查询评论
     * @param publishId 动态id
     * @param page 页码
     * @param pageSize 页大小
     * @return 评论
     */
    PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize);


    /**
     * 查询用户的评论数据
     * @param userId 用户id
     * @param type 类型
     * @param page 页码
     * @param pageSize 页大小
     * @return 评论集合
     */
    PageInfo<Comment> queryCommentListByUser(Long userId, Integer type, Integer page, Integer pageSize);

    /**
     * 通过redis查询推荐列表
     * @param pidList id
     * @return 集合
     */
    List<Publish> queryPublishByPids(List<Long> pidList);


    /**
     * 查询相册表
     *
     * @param userId 佳人id
     * @param page 页码
     * @param pageSize 页大小
     * @return 佳人相册
     */
    PageInfo<Publish> queryAlbumList(Long userId, Integer page, Integer pageSize);


}