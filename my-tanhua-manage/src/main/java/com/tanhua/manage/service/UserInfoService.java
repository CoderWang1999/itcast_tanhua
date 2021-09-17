package com.tanhua.manage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.manage.mapper.UserInfoMapper;
import com.tanhua.manage.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 */
@Service
@Repository
public class UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    /**
     * 补全佳人信息
     * @param  userId 佳人id
     * @return 佳人信息
     */
    public UserInfo queryUserInfoByUserId(Long userId) {
        //查询佳人信息并返回
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<UserInfo>();
        queryWrapper.eq("user_id",userId);
        return this.userInfoMapper.selectOne(queryWrapper);

    }

    /**
     * 查询推荐用户列表
     * @param queryWrapper 查询
     * @return 用户集合
     */
    public List<UserInfo> queryUserInfoList(QueryWrapper<UserInfo> queryWrapper) {
        return this.userInfoMapper.selectList(queryWrapper);
    }

    /**
     * 更改用户信息
     * @param userInfo 用户信息
     * @return true or false
     */
    public Boolean updateUserInfoByUserId(UserInfo userInfo) {
        //设置查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userInfo.getUserId());
        return this.userInfoMapper.update(userInfo, queryWrapper) > 0;
    }
}
