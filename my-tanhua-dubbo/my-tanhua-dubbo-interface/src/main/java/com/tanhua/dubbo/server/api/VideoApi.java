package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.dubbo.server.vo.PageInfo;

import java.util.List;


/**
 * @author ZJWzxy
 */
public interface VideoApi {

    /**
     * 保存小视频
     *
     * @param video 视频文件
     * @return true or false
     */
    String saveVideo(Video video);

    /**
     * 分页查询小视频列表，按照时间倒序排序
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return  小视频集合
     */
    PageInfo<Video> queryVideoList(Integer page, Integer pageSize);

    /**
     * 关注用户
     *
     * @param userId 用户id
     * @param followUserId 关注的用户id
     * @return true or false
     */
    Boolean followUser(Long userId, Long followUserId);

    /**
     * 取消关注用户
     *
     * @param userId 用户id
     * @param followUserId 关注的用户id
     * @return true or false
     */
    Boolean disFollowUser(Long userId, Long followUserId);

    /**
     * 通过redis查询推荐小视频列表
     * @param videoId 小视频id
     * @return 集合
     */
    Video queryVideoById(String videoId);

    /**
     * 根据vids批量查询视频列表
     * @param vids
     * @return
     */
    List<Video> queryVideoListByVids(List<Long> vids);

}
