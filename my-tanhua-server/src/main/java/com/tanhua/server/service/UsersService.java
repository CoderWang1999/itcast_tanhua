package com.tanhua.server.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.server.api.UserLikeApi;
import com.tanhua.dubbo.server.api.VisitorsApi;
import com.tanhua.dubbo.server.pojo.UserLike;
import com.tanhua.dubbo.server.pojo.Visitors;
import com.tanhua.server.enums.SexEnum;
import com.tanhua.server.pojo.Question;
import com.tanhua.server.pojo.Settings;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZJWzxy
 */
@Service
@SuppressWarnings("all")
public class UsersService {

    @Autowired
    private UserInfoService userInfoService;


    @Reference(version = "1.0.0")
    private UserLikeApi userLikeApi;


    @Autowired
    private RecommendUserService recommendUserService;

    @Reference(version = "1.0.0")
    private VisitorsApi visitorsApi;


    @Autowired
    private SettingsService settingsService;

    @Autowired
    private QuestionService questionService;

    /**
     * 查询个人信息
     * @param userID 用户id
     * @param huanxinID 环信id
     * @return 用户信息
     */
    public UserInfoVo queryUserInfo(String userID, String huanxinID) {
        User user = UserThreadLocal.get();
        Long userId = user.getId();
        if (StringUtils.isNotBlank(userID)) {
            userId = Long.valueOf(userID);
        } else if (StringUtils.isNotBlank(huanxinID)) {
            userId = Long.valueOf(huanxinID);
        }

        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(userId);
        if (null == userInfo) {
            return null;
        }
        //查询个人信息
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setAge(userInfo.getAge() != null ? userInfo.getAge().toString() : null);
        userInfoVo.setAvatar(userInfo.getLogo());
        userInfoVo.setBirthday(userInfo.getBirthday());
        userInfoVo.setEducation(userInfo.getEdu());
        userInfoVo.setCity(userInfo.getCity());
        userInfoVo.setGender(userInfo.getSex().name().toLowerCase());
        userInfoVo.setId(userInfo.getUserId());
        userInfoVo.setIncome(userInfo.getIncome() + "K");
        userInfoVo.setMarriage(StringUtils.equals(userInfo.getMarriage(), "已婚") ? 1 : 0);
        userInfoVo.setNickname(userInfo.getNickName());
        userInfoVo.setProfession(userInfo.getIndustry());

        return userInfoVo;
    }

    /**
     * 更改个人信息
     * @param userInfoVo 用户信息
     * @return true or false
     */
    public Boolean updateUserInfo(UserInfoVo userInfoVo) {
        User user = UserThreadLocal.get();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setAge(Integer.valueOf(userInfoVo.getAge()));
        userInfo.setSex(StringUtils.equalsIgnoreCase(userInfoVo.getGender(), "man") ? SexEnum.MAN : SexEnum.WOMAN);
        userInfo.setBirthday(userInfoVo.getBirthday());
        userInfo.setCity(userInfoVo.getCity());
        userInfo.setEdu(userInfoVo.getEducation());
        userInfo.setIncome(StringUtils.replaceAll(userInfoVo.getIncome(), "K", ""));
        userInfo.setIndustry(userInfoVo.getProfession());
        userInfo.setMarriage(userInfoVo.getMarriage() == 1 ? "已婚" : "未婚");
        return this.userInfoService.updateUserInfoByUserId(userInfo);
    }

    /**
     * 查询相互喜欢.喜欢,粉丝数量
     * @return 数量
     */
    public CountsVo queryCounts() {
        User user = UserThreadLocal.get();
        CountsVo countsVo = new CountsVo();

        countsVo.setEachLoveCount(this.userLikeApi.queryEachLikeCount(user.getId()));
        countsVo.setFanCount(this.userLikeApi.queryFanCount(user.getId()));
        countsVo.setLoveCount(this.userLikeApi.queryLikeCount(user.getId()));

        return countsVo;
    }


    /**
     * 互相关注、我关注、粉丝、谁看过我 - 翻页列表
     *
     * @param type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
     * @param page 页码
     * @param pageSize 页大小
     * @param nickname 昵称
     * @return 分页结果
     */
    public PageResult queryLikeList(Integer type, Integer page, Integer pageSize, String nickname) {
        //校验
        User user = UserThreadLocal.get();
        //设置分页结果
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        //获取所有的id
        // type: 1 互相关注 2 我关注 3 粉丝 4 谁看过我
        List<Long> userIds = new ArrayList<>();
        switch (type) {
            case 1: {
                List<UserLike> records = this.userLikeApi.queryEachLikeList(user.getId(), page, pageSize);
                for (UserLike record : records) {
                    userIds.add(record.getUserId());
                }

                break;
            }
            case 2: {
                List<UserLike> records = this.userLikeApi.queryLikeList(user.getId(), page, pageSize);
                for (UserLike record : records) {
                    userIds.add(record.getLikeUserId());
                }

                break;
            }
            case 3: {
                List<UserLike> records = this.userLikeApi.queryFanList(user.getId(), page, pageSize);
                for (UserLike record : records) {
                    userIds.add(record.getUserId());
                }

                break;
            }
            case 4: {
                List<Visitors> visitors = this.visitorsApi.topVisitor(user.getId(), page, pageSize);
                for (Visitors visitor : visitors) {
                    userIds.add(visitor.getVisitorUserId());
                }
                break;
            }
        }

        //判断是否为空
        if (CollectionUtils.isEmpty(userIds)) {
            return pageResult;
        }

        //设置查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotBlank(nickname)) {
            queryWrapper.like("nick_name", nickname);
        }
        //填充基本信息
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);

        List<UserLikeListVo> userLikeListVos = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            UserLikeListVo userLikeListVo = new UserLikeListVo();
            userLikeListVo.setAge(userInfo.getAge());
            userLikeListVo.setAvatar(userInfo.getLogo());
            userLikeListVo.setCity(userInfo.getCity());
            userLikeListVo.setEducation(userInfo.getEdu());
            userLikeListVo.setGender(userInfo.getSex().name().toLowerCase());
            userLikeListVo.setId(userInfo.getUserId());
            userLikeListVo.setMarriage(StringUtils.equals(userInfo.getMarriage(), "已婚") ? 1 : 0);
            userLikeListVo.setNickname(userInfo.getNickName());

            Double score = this.recommendUserService.queryScore(userInfo.getUserId(), user.getId());
            if(score == 0){
                score = RandomUtils.nextDouble(30, 90);
            }
            userLikeListVo.setMatchRate(score.intValue());

            userLikeListVos.add(userLikeListVo);
        }

        pageResult.setItems(userLikeListVos);
        return pageResult;
    }

    /**
     * 取消喜欢
     * @param userId 用户id
     */
    public void disLike(Long userId) {
        User user = UserThreadLocal.get();
        this.userLikeApi.deleteUserLike(user.getId(), userId);
    }

    /**
     * 喜欢粉丝
     * @param userId
     */
    public void likeFan(Long userId) {
        User user = UserThreadLocal.get();
        this.userLikeApi.saveUserLike(user.getId(), userId);
}


    /**
     * 查询配置
      * @return
     */
    public SettingsVo querySettings() {
        User user = UserThreadLocal.get();
        // 查询配置
        Settings settings = this.settingsService.querySettings(user.getId());
        SettingsVo settingsVo = new SettingsVo();
        settingsVo.setId(user.getId());
        settingsVo.setPhone(user.getMobile());
        if (null != settings) {
            settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
            settingsVo.setLikeNotification(settings.getLikeNotification());
            settingsVo.setPinglunNotification(settings.getPinglunNotification());
        }
        // 查询设置的问题
        Question question = this.questionService.queryQuestion(user.getId());
        if (null != question) {
            settingsVo.setStrangerQuestion(question.getTxt());
        }
        return settingsVo;
    }
}