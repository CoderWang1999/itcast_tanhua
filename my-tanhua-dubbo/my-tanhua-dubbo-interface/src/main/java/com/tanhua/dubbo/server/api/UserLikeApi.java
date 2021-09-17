package com.tanhua.dubbo.server.api;


import com.tanhua.dubbo.server.pojo.UserLike;

import java.util.List;

/**
 * @author ZJWzxy
 */
public interface UserLikeApi {

    /**
     * 保存喜欢
     *
     * @param userId 自己id
     * @param likeUserId 对方id
     * @return  String
     */
    String saveUserLike(Long userId, Long likeUserId);


    /**
     * 相互喜欢
     *
     * @param userId 自己id
     * @param likeUserId 对方id
     * @return true or false
     */
    Boolean isMutualLike(Long userId, Long likeUserId);

    /**
     * 删除用户喜欢
     *
     * @param userId 自己id
     * @param likeUserId 对方id
     * @return true or false
     */
    Boolean deleteUserLike(Long userId, Long likeUserId);


    /**
     * 相互喜欢的数量
     *@param userId  用户id
     * @return 数量
     */
    Long queryEachLikeCount(Long userId);

    /**
     * 喜欢数
     *@param userId  用户id
     * @return 数量
     */
    Long queryLikeCount(Long userId);

    /**
     * 粉丝数
     *@param userId  用户id
     * @return 数量
     */
    Long queryFanCount(Long userId);

    /**
     * 查询相互喜欢列表
     *
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    List<UserLike> queryEachLikeList(Long userId, Integer page, Integer pageSize);

    /**
     * 查询我喜欢的列表
     *
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    List<UserLike> queryLikeList(Long userId, Integer page, Integer pageSize);

    /**
     * 查询粉丝列表
     *
     * @param userId 用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 喜欢
     */
    List<UserLike> queryFanList(Long userId, Integer page, Integer pageSize);

}
