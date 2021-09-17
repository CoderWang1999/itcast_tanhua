package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.enums.SexEnum;
import com.tanhua.sso.mapper.UserInfoMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.pojo.UserInfo;
import com.tanhua.sso.vo.PicUploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author ZJWzxy
 * @date 2021/04/09
 */
@Repository
@Service
public class UserInfoService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private FaceEngineService faceEngineService;

    @Autowired
    private PicUploadService picUploadService;

    /**
     * 第一次登录完善用户基本信息
     *
     * @param param 基本信息
     * @param token 加密数据
     * @return true or false
     */
    public Boolean saveUserInfo(Map<String, String> param, String token) {
        //校验token
        User user = this.userService.queryUserToken(token);
        //判断是否为新用户
        if (null == user) {
            //不是新用户
            return false;
        }

        //新用户
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        //设置用户的性别
        userInfo.setSex(StringUtils.equalsIgnoreCase(param.get("gender"), "man") ? SexEnum.MAN : SexEnum.WOMAN);
        //设置用户的昵称
        userInfo.setNickName(param.get("nickname"));
        //设置用户的生日
        userInfo.setBirthday(param.get("birthday"));
        //设置用户所在城市
        userInfo.setCity(param.get("city"));
        //向数据库中新增
        return this.userInfoMapper.insert(userInfo) == 1;


    }

    /**
     * 保存用户头像
     *
     * @param file  头像文件
     * @param token 加密数据
     * @return true or false
     */
    public Boolean saveUserLogo(MultipartFile file, String token) {
        //校验token
        User user = userService.queryUserToken(token);
        //判断是否是新用户
        if (null == user) {
            //不是新用户
            return false;
        }
        //新用户
        try {
            //校验图片是否为人像,不是返回false
            boolean bool = this.faceEngineService.checkIsPortrait(file.getBytes());
            if (!bool) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //图片是人像,上传到阿里云OSS
        PicUploadResult result = this.picUploadService.upload(file);
        if (StringUtils.isEmpty(result.getName())) {
            //上传失败
            return false;
        }
        //上传成功将logo保存到用户信息中
        UserInfo userInfo = new UserInfo();
        //从阿里云得到图片保存到用户信息中
        userInfo.setLogo(result.getName());
        //将从token中的得到的用户id与数据库中的id作比较,找到需要修改的用户
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        //修改用户信息
        return this.userInfoMapper.update(userInfo, queryWrapper) == 1;

    }
}
