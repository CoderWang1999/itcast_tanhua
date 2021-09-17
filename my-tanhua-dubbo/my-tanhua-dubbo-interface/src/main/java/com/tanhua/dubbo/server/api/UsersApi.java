package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.vo.PageInfo;

import java.util.List;

/**
 * @author ZJWzxy
 */
public interface UsersApi {

    /**
     * 保存好友
     *
     * @param users 好友
     * @return  字符串
     */
    String saveUsers(Users users);

    /**
     * 根据用户id查询Users列表
     *
     * @param userId 用户id
     * @return 集合
     */
    List<Users> queryAllUsersList(Long userId);

    /**
     * 根据用户id查询Users列表(分页查询)
     *
     * @param userId 用户id
     * @param page  页码
     * @param pageSize  页大小
     * @return 分页结果
     */
    PageInfo<Users> queryUsersList(Long userId, Integer page, Integer pageSize);
}
