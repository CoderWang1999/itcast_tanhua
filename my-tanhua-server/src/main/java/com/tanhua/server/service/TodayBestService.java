package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.dubbo.server.api.UserLikeApi;
import com.tanhua.dubbo.server.api.UserLocationApi;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.dubbo.server.vo.UserLocationVo;
import com.tanhua.server.enums.SexEnum;
import com.tanhua.server.pojo.Question;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.NearUserVo;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.RecommendUserQueryParam;
import com.tanhua.server.vo.TodayBest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author ZJWzxy
 * @date 2021/04/13
 */
@Service
@Repository
@Slf4j
public class TodayBestService {

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendUserService recommendUserService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${tanhua.sso.default.user}")
    private Long defaultUser;

    @Value("${tanhua.sso.default.recommend.users}")
    private String defaultRecommendUsers;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tanhua.sso.url}")
    private String url;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Reference(version = "1.0.0")
    private UserLocationApi userLocationApi;


    @Reference(version = "1.0.0")
    private UserLikeApi userLikeApi;

    @Autowired
    private IMService imService;


    /**
     * 查询今日佳人
     *
     * @return 返回todayBest
     */
    public TodayBest queryTodayBest() {
        //校验token是否有效
        User user = UserThreadLocal.get();
        //token有效,查询推荐用户---今日佳人
        TodayBest todayBest = this.recommendUserService.queryTodayBest(user.getId());
        //判断
        if (null == todayBest) {
            //没有查询到今日佳人,给出默认的推荐用户
            todayBest = new TodayBest();
            todayBest.setId(defaultUser);
            //默认用户固定缘分值
            todayBest.setFateValue(80L);
        }
        //补全个人信息
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(todayBest.getId());
        //判断
        if (null == userInfo) {
            //因为各种原因没有拿到信息
            return null;
        }
        //拿到信息,将佳人的信息补全
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
        todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
        todayBest.setAge(userInfo.getAge());
        //返回佳人
        return todayBest;
    }

    /**
     * 查询推荐用户列表
     *
     * @param queryParam 分页参数
     * @return 分页后的结果
     */
    public PageResult queryRecommendation(RecommendUserQueryParam queryParam) {
        //校验token
        //校验token是否有效
        User user = UserThreadLocal.get();
        //token有效,填充分页默认信息
        PageResult pageResult = new PageResult();
        pageResult.setPage(queryParam.getPage());
        pageResult.setPagesize(queryParam.getPagesize());

        PageInfo<RecommendUser> pageInfo = this.recommendUserService.queryRecommendUserList(user.getId(), queryParam.getPage(), queryParam.getPagesize());
        List<RecommendUser> records = pageInfo.getRecords();
        //如果没有查询到,需要使用推荐列表
        if (CollectionUtils.isEmpty(records)) {
            //默认推荐列表
            String[] ss = StringUtils.split(defaultRecommendUsers, ',');
            for (String s : ss) {
                if (Long.valueOf(s) != UserThreadLocal.get().getId().longValue()) {
                    RecommendUser recommendUser = new RecommendUser();
                    recommendUser.setUserId(Long.valueOf(s));
                    recommendUser.setToUserId(user.getId());
                    recommendUser.setScore(RandomUtils.nextDouble(70, 99));

                    records.add(recommendUser);
                }
            }
        }
        if (CollectionUtils.isEmpty(records)) {
            //没有查询到推荐的用户列表
            String msg = "推荐列表数据中没有数据";
            log.info(msg);
            return pageResult;
        }
        //查询到推荐用户列表,填充个人信息
        //收集推荐用户的id
        Set<Long> userIds = new HashSet<>();
        for (RecommendUser record : records) {
            userIds.add(record.getUserId());
        }
        //构建查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        //用户id参数
        queryWrapper.in("user_id", userIds);
        /*if (StringUtils.isNotEmpty(queryParam.getGender())) {
            //需要性别参数的查询
            queryWrapper.eq("sex", StringUtils.equals(queryParam.getGender(), "man") ? 1 : 2);
        }
        if (StringUtils.isNotEmpty(queryParam.getCity())) {
            //需要城市参数查询
            queryWrapper.like("city", queryParam.getCity());
        }
        if (queryParam.getAge() != null) {
            //设置年龄参数
            queryWrapper.le("age", queryParam.getAge());
        }*/
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        //判读集合
        if (userInfoList == null) {
            //没有查询到用户基本信息
            return pageResult;
        }
        List<TodayBest> todayBests = new ArrayList<>();
        //查询到
        for (UserInfo userInfo : userInfoList) {
            TodayBest todayBest = new TodayBest();
            //填充数据
            todayBest.setId(userInfo.getUserId());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setNickname(userInfo.getNickName());
            todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
            todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
            todayBest.setAge(userInfo.getAge());
            //拿到缘分值
            for (RecommendUser record : records) {
                if (record.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    //缘分值取整
                    double score = Math.floor(record.getScore());
                    todayBest.setFateValue(Double.valueOf(score).longValue());
                    break;
                }
            }
            //添加到集合中
            todayBests.add(todayBest);
        }
        //按照缘分值进行倒序排序
        Collections.sort(todayBests, ((o1, o2) -> new Long(o2.getFateValue() - o1.getFateValue()).intValue()));
        pageResult.setItems(todayBests);
        return pageResult;
    }

    /**
     * 查询今日佳人信息
     *
     * @param userId 佳人id
     * @return 佳人信息
     */
    public TodayBest queryTodayBest(Long userId) {

        //校验
        User user = UserThreadLocal.get();

        TodayBest todayBest = new TodayBest();
        //补全信息
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(userId);
        todayBest.setId(userId);
        todayBest.setAge(userInfo.getAge());
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setGender(userInfo.getSex().name().toLowerCase());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));

        double score = this.recommendUserService.queryScore(userId, user.getId());
        if (score == 0) {
            //默认分值
            score = 98;
        }

        todayBest.setFateValue(Double.valueOf(score).longValue());

        return todayBest;
    }


    /**
     * 查询设置的问题
     *
     * @param userId 用户id
     * @return 问题字符串
     */
    public String queryQuestion(Long userId) {
        Question question = this.questionService.queryQuestion(userId);
        if (null != question) {
            return question.getTxt();
        }
        return "";
    }


    /**
     * 回复陌生人问题，发送消息给对方
     *
     * @param userId 陌生人id
     * @param reply  回复内容
     * @return true or false
     */
    public Boolean replyQuestion(Long userId, String reply) {
        User user = UserThreadLocal.get();
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(user.getId());

        //构建消息内容
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", user.getId().toString());
        msg.put("nickname", this.queryQuestion(userId));
        msg.put("strangerQuestion", userInfo.getNickName());
        msg.put("reply", reply);

        try {
            String msgStr = MAPPER.writeValueAsString(msg);

            String targetUrl = this.url + "/user/huanxin/messages";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("target", userId.toString());
            params.add("msg", msgStr);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<Void> responseEntity = this.restTemplate.postForEntity(targetUrl, httpEntity, Void.class);

            return responseEntity.getStatusCodeValue() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    /**
     * 搜索附近的人
     *
     * @param gender   性别
     * @param distance 距离
     * @return 附近的人
     */
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        User user = UserThreadLocal.get();
        //先查询出用户自己的经纬度
        UserLocationVo userLocationVo = userLocationApi.queryByUserId(user.getId());
        Double longitude = userLocationVo.getLongitude();
        Double latitude = userLocationVo.getLatitude();
        //调用dubbo获取范围内的用户
        List<UserLocationVo> userLocationVoList = this.userLocationApi.queryUserFromLocation(longitude, latitude, Integer.valueOf(distance));
        if (CollectionUtils.isEmpty(userLocationVoList)) {
            //查询为空
            return Collections.emptyList();
        }
        //查询到
        Set<Long> userIds = new HashSet<>();
        //得到所有的用户id
        for (UserLocationVo locationVo : userLocationVoList) {
            userIds.add(locationVo.getUserId());
        }
        //设置查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.equalsIgnoreCase(gender, "man")) {
            queryWrapper.eq("sex", SexEnum.MAN);
        } else if (StringUtils.equalsIgnoreCase(gender, "woman")) {
            queryWrapper.eq("sex", SexEnum.WOMAN);
        }
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        List<NearUserVo> nearUserVoList = new ArrayList<>();
        for (UserLocationVo locationVo : userLocationVoList) {
            //排除自己
            if (locationVo.getUserId().longValue() == user.getId().longValue()) {
                continue;
            }
            for (UserInfo userInfo : userInfoList) {
                if (locationVo.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    NearUserVo nearUserVo = new NearUserVo();
                    nearUserVo.setUserId(userInfo.getUserId());
                    nearUserVo.setAvatar(userInfo.getLogo());
                    nearUserVo.setNickname(userInfo.getNickName());
                    nearUserVoList.add(nearUserVo);
                    break;
                }
            }
        }
        //最后返回数据
        return nearUserVoList;
    }

    /**
     * 探花
     *
     * @return 佳人
     */
    public List<TodayBest> queryCardsList() {
        //校验
        User user = UserThreadLocal.get();
        Integer count = 50;
        //调用RecommendUserService中的方法查询
        PageInfo<RecommendUser> pageInfo = this.recommendUserService.queryRecommendUserList(user.getId(), 1, count);
        List<RecommendUser> records = pageInfo.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            //查询失败,使用默认推荐列表
            //默认推荐列表
            String[] ss = StringUtils.split(defaultRecommendUsers, ',');
            for (String s : ss) {
                //填充默认信息
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Long.valueOf(s));
                recommendUser.setToUserId(user.getId());
                records.add(recommendUser);
            }
        }
        List<RecommendUser> newRecommendUserList = new ArrayList<>();
        //设置随机展示的条数,最大为10
        int showCount = Math.min(10, records.size());
        for (int i = 0; i < showCount; i++) {
            createRecommendUser(newRecommendUserList, records);
        }
        Set<Long> userIds = new HashSet<>();
        for (RecommendUser recommendUser : newRecommendUserList) {
            userIds.add(recommendUser.getUserId());
        }
        //查询学生信息
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        List<TodayBest> todayBestList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            TodayBest todayBest = new TodayBest();
            todayBest.setId(userInfo.getUserId());
            todayBest.setAge(userInfo.getAge());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setGender(userInfo.getSex().name().toLowerCase());
            todayBest.setNickname(userInfo.getNickName());
            todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
            todayBest.setFateValue(0L);

            todayBestList.add(todayBest);
        }
        return todayBestList;
    }

    /**
     * 随机递归生成推荐好友,保证不重复
     */
    private void createRecommendUser(List<RecommendUser> newRecommendUserList,
                                     List<RecommendUser> records) {

        RecommendUser recommendUser = records.get(RandomUtils.nextInt(0, records.size() - 1));
        //去重
        if (!newRecommendUserList.contains(recommendUser)) {
            //不包含
            newRecommendUserList.add(recommendUser);
        } else {
            //包含,递归
            createRecommendUser(newRecommendUserList, records);
        }
    }


    /**
     * 喜欢
     *
     * @param likeUserId 喜欢人的id
     * @return true or false
     */
    public Boolean likeUser(Long likeUserId) {
        //校验
        User user = UserThreadLocal.get();
        String id = this.userLikeApi.saveUserLike(user.getId(), likeUserId);
        if (StringUtils.isEmpty(id)) {
            return false;
        }

        if (this.userLikeApi.isMutualLike(user.getId(), likeUserId)) {
            //相互喜欢成为好友
            this.imService.contactUser(likeUserId);
        }
        return true;
    }


    /**
     * 不喜欢
     * @param likeUserId 喜欢人的id
     * @return true or false
     */
    public Boolean disLikeUser(Long likeUserId) {
        //校验
        User user = UserThreadLocal.get();
        //删除喜欢
        return this.userLikeApi.deleteUserLike(user.getId(), likeUserId);
    }
}
