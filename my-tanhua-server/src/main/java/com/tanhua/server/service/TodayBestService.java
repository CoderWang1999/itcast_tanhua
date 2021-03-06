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
     * ??????????????????
     *
     * @return ??????todayBest
     */
    public TodayBest queryTodayBest() {
        //??????token????????????
        User user = UserThreadLocal.get();
        //token??????,??????????????????---????????????
        TodayBest todayBest = this.recommendUserService.queryTodayBest(user.getId());
        //??????
        if (null == todayBest) {
            //???????????????????????????,???????????????????????????
            todayBest = new TodayBest();
            todayBest.setId(defaultUser);
            //???????????????????????????
            todayBest.setFateValue(80L);
        }
        //??????????????????
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(todayBest.getId());
        //??????
        if (null == userInfo) {
            //????????????????????????????????????
            return null;
        }
        //????????????,????????????????????????
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
        todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
        todayBest.setAge(userInfo.getAge());
        //????????????
        return todayBest;
    }

    /**
     * ????????????????????????
     *
     * @param queryParam ????????????
     * @return ??????????????????
     */
    public PageResult queryRecommendation(RecommendUserQueryParam queryParam) {
        //??????token
        //??????token????????????
        User user = UserThreadLocal.get();
        //token??????,????????????????????????
        PageResult pageResult = new PageResult();
        pageResult.setPage(queryParam.getPage());
        pageResult.setPagesize(queryParam.getPagesize());

        PageInfo<RecommendUser> pageInfo = this.recommendUserService.queryRecommendUserList(user.getId(), queryParam.getPage(), queryParam.getPagesize());
        List<RecommendUser> records = pageInfo.getRecords();
        //?????????????????????,????????????????????????
        if (CollectionUtils.isEmpty(records)) {
            //??????????????????
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
            //????????????????????????????????????
            String msg = "?????????????????????????????????";
            log.info(msg);
            return pageResult;
        }
        //???????????????????????????,??????????????????
        //?????????????????????id
        Set<Long> userIds = new HashSet<>();
        for (RecommendUser record : records) {
            userIds.add(record.getUserId());
        }
        //??????????????????
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        //??????id??????
        queryWrapper.in("user_id", userIds);
        /*if (StringUtils.isNotEmpty(queryParam.getGender())) {
            //???????????????????????????
            queryWrapper.eq("sex", StringUtils.equals(queryParam.getGender(), "man") ? 1 : 2);
        }
        if (StringUtils.isNotEmpty(queryParam.getCity())) {
            //????????????????????????
            queryWrapper.like("city", queryParam.getCity());
        }
        if (queryParam.getAge() != null) {
            //??????????????????
            queryWrapper.le("age", queryParam.getAge());
        }*/
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        //????????????
        if (userInfoList == null) {
            //?????????????????????????????????
            return pageResult;
        }
        List<TodayBest> todayBests = new ArrayList<>();
        //?????????
        for (UserInfo userInfo : userInfoList) {
            TodayBest todayBest = new TodayBest();
            //????????????
            todayBest.setId(userInfo.getUserId());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setNickname(userInfo.getNickName());
            todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
            todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
            todayBest.setAge(userInfo.getAge());
            //???????????????
            for (RecommendUser record : records) {
                if (record.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    //???????????????
                    double score = Math.floor(record.getScore());
                    todayBest.setFateValue(Double.valueOf(score).longValue());
                    break;
                }
            }
            //??????????????????
            todayBests.add(todayBest);
        }
        //?????????????????????????????????
        Collections.sort(todayBests, ((o1, o2) -> new Long(o2.getFateValue() - o1.getFateValue()).intValue()));
        pageResult.setItems(todayBests);
        return pageResult;
    }

    /**
     * ????????????????????????
     *
     * @param userId ??????id
     * @return ????????????
     */
    public TodayBest queryTodayBest(Long userId) {

        //??????
        User user = UserThreadLocal.get();

        TodayBest todayBest = new TodayBest();
        //????????????
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(userId);
        todayBest.setId(userId);
        todayBest.setAge(userInfo.getAge());
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setGender(userInfo.getSex().name().toLowerCase());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));

        double score = this.recommendUserService.queryScore(userId, user.getId());
        if (score == 0) {
            //????????????
            score = 98;
        }

        todayBest.setFateValue(Double.valueOf(score).longValue());

        return todayBest;
    }


    /**
     * ?????????????????????
     *
     * @param userId ??????id
     * @return ???????????????
     */
    public String queryQuestion(Long userId) {
        Question question = this.questionService.queryQuestion(userId);
        if (null != question) {
            return question.getTxt();
        }
        return "";
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param userId ?????????id
     * @param reply  ????????????
     * @return true or false
     */
    public Boolean replyQuestion(Long userId, String reply) {
        User user = UserThreadLocal.get();
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(user.getId());

        //??????????????????
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
     * ??????????????????
     *
     * @param gender   ??????
     * @param distance ??????
     * @return ????????????
     */
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        User user = UserThreadLocal.get();
        //????????????????????????????????????
        UserLocationVo userLocationVo = userLocationApi.queryByUserId(user.getId());
        Double longitude = userLocationVo.getLongitude();
        Double latitude = userLocationVo.getLatitude();
        //??????dubbo????????????????????????
        List<UserLocationVo> userLocationVoList = this.userLocationApi.queryUserFromLocation(longitude, latitude, Integer.valueOf(distance));
        if (CollectionUtils.isEmpty(userLocationVoList)) {
            //????????????
            return Collections.emptyList();
        }
        //?????????
        Set<Long> userIds = new HashSet<>();
        //?????????????????????id
        for (UserLocationVo locationVo : userLocationVoList) {
            userIds.add(locationVo.getUserId());
        }
        //??????????????????
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
            //????????????
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
        //??????????????????
        return nearUserVoList;
    }

    /**
     * ??????
     *
     * @return ??????
     */
    public List<TodayBest> queryCardsList() {
        //??????
        User user = UserThreadLocal.get();
        Integer count = 50;
        //??????RecommendUserService??????????????????
        PageInfo<RecommendUser> pageInfo = this.recommendUserService.queryRecommendUserList(user.getId(), 1, count);
        List<RecommendUser> records = pageInfo.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            //????????????,????????????????????????
            //??????????????????
            String[] ss = StringUtils.split(defaultRecommendUsers, ',');
            for (String s : ss) {
                //??????????????????
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Long.valueOf(s));
                recommendUser.setToUserId(user.getId());
                records.add(recommendUser);
            }
        }
        List<RecommendUser> newRecommendUserList = new ArrayList<>();
        //???????????????????????????,?????????10
        int showCount = Math.min(10, records.size());
        for (int i = 0; i < showCount; i++) {
            createRecommendUser(newRecommendUserList, records);
        }
        Set<Long> userIds = new HashSet<>();
        for (RecommendUser recommendUser : newRecommendUserList) {
            userIds.add(recommendUser.getUserId());
        }
        //??????????????????
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
     * ??????????????????????????????,???????????????
     */
    private void createRecommendUser(List<RecommendUser> newRecommendUserList,
                                     List<RecommendUser> records) {

        RecommendUser recommendUser = records.get(RandomUtils.nextInt(0, records.size() - 1));
        //??????
        if (!newRecommendUserList.contains(recommendUser)) {
            //?????????
            newRecommendUserList.add(recommendUser);
        } else {
            //??????,??????
            createRecommendUser(newRecommendUserList, records);
        }
    }


    /**
     * ??????
     *
     * @param likeUserId ????????????id
     * @return true or false
     */
    public Boolean likeUser(Long likeUserId) {
        //??????
        User user = UserThreadLocal.get();
        String id = this.userLikeApi.saveUserLike(user.getId(), likeUserId);
        if (StringUtils.isEmpty(id)) {
            return false;
        }

        if (this.userLikeApi.isMutualLike(user.getId(), likeUserId)) {
            //????????????????????????
            this.imService.contactUser(likeUserId);
        }
        return true;
    }


    /**
     * ?????????
     * @param likeUserId ????????????id
     * @return true or false
     */
    public Boolean disLikeUser(Long likeUserId) {
        //??????
        User user = UserThreadLocal.get();
        //????????????
        return this.userLikeApi.deleteUserLike(user.getId(), likeUserId);
    }
}
