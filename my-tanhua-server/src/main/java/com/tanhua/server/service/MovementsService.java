package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.VisitorsApi;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.Visitors;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.RelativeDateFormat;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Movements;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.PicUploadResult;
import com.tanhua.server.vo.VisitorsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 */
@Service
@Slf4j
public class MovementsService {

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private UserService userService;

    @Autowired
    private PicUploadService picUploadService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Reference(version = "1.0.0")
    private VisitorsApi visitorsApi;

    /**
     * 发布动态
     *
     * @param textContent   文字动态
     * @param location      位置
     * @param longitude     经度
     * @param latitude      纬度
     * @param multipartFile 图片动态（支持多张图片）
     * @return 状态码
     */
    public String savePublish(String textContent, String location, String longitude, String latitude, MultipartFile[] multipartFile) {
        //校验
        User user = UserThreadLocal.get();
        try {
            //创建一个新动态
            Publish publish = new Publish();
            //填充其他的信息
            publish.setText(textContent);
            publish.setLocationName(location);
            publish.setLongitude(longitude);
            publish.setLatitude(latitude);
            publish.setUserId(user.getId());
            publish.setCreated(System.currentTimeMillis());
            //创建一个集合用来保存图片
            List<String> images = new ArrayList<>();
            //上传图片
            for (MultipartFile file : multipartFile) {
                //调用阿里云服务器
                PicUploadResult uploadResult = this.picUploadService.upload(file);
                images.add(uploadResult.getName());
            }
            //添加图片到动态
            publish.setMedias(images);
            //调用QuanZiImpl中的方法进行保存到MonGoDB
            String publishId = this.quanZiApi.savePublish(publish);
            if (StringUtils.isNotEmpty(publishId)) {
                //保存成功
                String msg = "图片上传成功";
                log.info(msg, images.toString());
                return publishId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询推荐动态
     *
     * @param page     页码
     * @param pageSize 每页显示条数
     * @return 分页后的数据结果
     */
    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {

        return this.queryPublishList(page, pageSize, true);
    }

    /**
     * 查询好友动态
     *
     * @param page     页码
     * @param pageSize 每页显示条数
     * @return 分页后的数据结果
     */
    public PageResult queryUserPublishList(Integer page, Integer pageSize) {
        return this.queryPublishList(page, pageSize, false);
    }

    /**
     * 查询动态公共方法
     *
     * @param page     页码
     * @param pageSize 每页显示条数
     * @return 分页后的数据结果
     */
    private PageResult queryPublishList(Integer page, Integer pageSize, boolean isRecommend) {
        PageResult pageResult = new PageResult();
        PageInfo<Publish> pageInfo = null;
        //默认查询得是推荐
        User user = UserThreadLocal.get();
        Long userId = isRecommend ? null : user.getId();
        if (isRecommend) {
            //查询推荐动态
            String value = this.redisTemplate.opsForValue().get("QUANZI_PUBLISH_RECOMMEND_" + UserThreadLocal.get().getId());
            if (StringUtils.isNotEmpty(value)) {
                String[] pids = StringUtils.split(value, ',');
                int startIndex = (page - 1) * pageSize;
                if (startIndex < pids.length) {
                    int endIndex = startIndex + pageSize - 1;
                    if (endIndex >= pids.length) {
                        endIndex = pids.length - 1;
                    }

                    List<Long> pidList = new ArrayList<>();
                    for (int i = startIndex; i <= endIndex; i++) {
                        pidList.add(Long.valueOf(pids[i]));
                    }

                    List<Publish> publishList = this.quanZiApi.queryPublishByPids(pidList);
                    pageInfo = new PageInfo<>();
                    pageInfo.setRecords(publishList);
                }

            }
        }
        //查询默认动态
        if (null == pageInfo) {
            if (!isRecommend) {
                userId = user.getId();
            }
        }
        //调用QuanZiApi查询MonGoDb中的好友动态信息
        pageInfo = this.quanZiApi.queryPublishList(userId, page, pageSize);
        //设置分页结果基本信息
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);
        pageResult.setCounts(0);
        pageResult.setPages(0);
        //查询动态,得到数据列表
        List<Publish> records = pageInfo.getRecords();
        //判断
        if (CollectionUtils.isEmpty(records)) {
            //没有动态信息
            String msg = "动态列表为空";
            log.info(msg);
            return pageResult;
        }
        List<Movements> movementsList = new ArrayList<>();
        //将查询到的动态信息封装
        for (Publish record : records) {
            Movements movements = new Movements();
            //得到动态的id
            movements.setId(record.getId().toHexString());
            //得到动态的图片
            movements.setImageContent(record.getMedias().toArray(new String[]{}));
            //得到动态的文字评论
            movements.setTextContent(record.getText());
            //得到发布动态的用户的id
            movements.setUserId(record.getUserId());
            //得到动态的发布时间
            movements.setCreateDate(RelativeDateFormat.format(new Date(record.getCreated())));

            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + movements.getId();
            String likeCommentKey = "QUANZI_COMMENT_LIKE_" + movements.getId();

            String loveUserCommentKey = "QUANZI_COMMENT_LOVE_USER_" + user.getId() + "_" + movements.getId();
            String loveCommentKey = "QUANZI_COMMENT_LOVE_" + movements.getId();
            //TODO 评论数
            movements.setCommentCount(10);
            //TODO 距离
            movements.setDistance("1.2公里");
            //是否点赞（1是，0否）
            movements.setHasLiked(this.redisTemplate.hasKey(likeUserCommentKey) ? 1 : 0);
            //是否喜欢（1是，0否）
            movements.setHasLoved(this.redisTemplate.hasKey(loveUserCommentKey) ? 1 : 0);
            // 点赞数
            String value1 = this.redisTemplate.opsForValue().get(likeCommentKey);
            if (StringUtils.isNotEmpty(value1)) {
                movements.setLikeCount(Integer.valueOf(value1));
            } else {
                //如果为空
                movements.setLikeCount(0);
            }

            //喜欢数
            String value2 = this.redisTemplate.opsForValue().get(loveCommentKey);
            if (StringUtils.isNotEmpty(value2)) {
                movements.setLoveCount(Integer.valueOf(value2));
            } else {
                //如果为空
                movements.setLoveCount(0);
            }
            //添加到动态集合
            movementsList.add(movements);
        }
        //得到所有的用户id
        List<Long> userIds = new ArrayList<>();
        for (Movements movements : movementsList) {
            //去重
            if (!userIds.contains(movements.getUserId())) {
                userIds.add(movements.getUserId());
            }
        }
        //通过mySQL数据库补充好友信息
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        for (Movements movements : movementsList) {
            for (UserInfo userInfo : userInfoList) {
                if (movements.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    movements.setAge(userInfo.getAge());
                    movements.setAvatar(userInfo.getLogo());
                    movements.setGender(userInfo.getSex().name().toLowerCase());
                    movements.setNickname(userInfo.getNickName());
                    movements.setTags(StringUtils.split(userInfo.getTags(), ','));
                    break;
                }
            }
        }
        pageResult.setItems(movementsList);
        return pageResult;
    }

    /**
     * 查询点赞数
     *
     * @param publishId 发布id
     * @return 点赞数
     */
    public Long likeComment(String publishId) {
        //校验token
        User user = UserThreadLocal.get();
        //查询
        boolean bool = this.quanZiApi.saveLikeComment(user.getId(), publishId);
        if (!bool) {
            //保存失败
            return null;
        }
        //保存成功,获取点赞数----通过redis
        String likeCommentKey = "QUANZI_COMMENT_LIKE_" + publishId;
        Long likeCount = 0L;
        Long count = 0L;
        if (!this.redisTemplate.hasKey(likeCommentKey)) {
            //不存在redis
            count = this.quanZiApi.queryCommentCount(publishId, 1);
            likeCount = count;
            this.redisTemplate.opsForValue().set(likeCommentKey, String.valueOf(likeCount + 1));
        } else {
            //存在
            likeCount = this.redisTemplate.opsForValue().increment(likeCommentKey);
        }
        //记录当前用户已经点赞
        String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + publishId;
        //对是否点赞进行标记
        this.redisTemplate.opsForValue().set(likeUserCommentKey, "1");
        return likeCount;
    }

    /**
     * 取消点赞
     *
     * @param publishId 动态id
     * @return 点赞数
     */
    public Long disLikeComment(String publishId) {
        //校验token
        User user = UserThreadLocal.get();
        //调用quanziz中的api进行取消
        boolean bool = this.quanZiApi.removeComment(user.getId(), publishId, 1);
        if (!bool) {
            //取消失败
            return null;
        }
        //取消成功,redis中的点赞数减去1
        // redis中的点赞数需要减少1
        String likeCommentKey = "QUANZI_COMMENT_LIKE_" + publishId;
        Long count = this.redisTemplate.opsForValue().decrement(likeCommentKey);
        // 删除该用户的标记点赞
        String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + publishId;
        this.redisTemplate.delete(likeUserCommentKey);
        return count;
    }

    /**
     * 喜欢
     *
     * @param publishId 动态id
     * @return 数量
     */
    public Long loveComment(String publishId) {
        //校验
        User user = UserThreadLocal.get();
        //调用方法进行喜欢
        boolean bool = this.quanZiApi.saveLoveComment(user.getId(), publishId);
        //判断
        if (!bool) {
            //保存失败
            return null;
        }
        //保存成功
        //初始化参数
        Long loveCount = 0L;
        Long count = 0L;
        String loveCommentKey = "QUANZI_COMMENT_LOVE_" + publishId;
        //判断redis中是否有
        if (!this.redisTemplate.hasKey(loveCommentKey)) {
            //redis中没有
            count = this.quanZiApi.queryCommentCount(publishId, 3);
            loveCount = count;
            //添加到redis
            this.redisTemplate.opsForValue().set(loveCommentKey, String.valueOf(loveCount + 1));
        } else {
            //redis中有
            loveCount = this.redisTemplate.opsForValue().increment(loveCommentKey);
        }
        // 标记当前用于已经喜欢
        String loveUserCommentKey = "QUANZI_COMMENT_LOVE_USER_" + user.getId() + "_" + publishId;
        this.redisTemplate.opsForValue().set(loveUserCommentKey, "1");

        return loveCount;
    }

    /**
     * 取消喜欢
     *
     * @param publishId 动态id
     * @return 取消数量
     */
    public Long unLoveComment(String publishId) {
        //校验
        User user = UserThreadLocal.get();
        boolean bool = this.quanZiApi.removeComment(user.getId(), publishId, 3);
        if (!bool) {
            //取消失败
            return null;
        }

        // redis中的喜欢数需要减少1
        String loveCommentKey = "QUANZI_COMMENT_LOVE_" + publishId;
        Long count = this.redisTemplate.opsForValue().decrement(loveCommentKey);

        // 删除该用户的标记喜欢
        String loveUserCommentKey = "QUANZI_COMMENT_LOVE_USER_" + user.getId() + "_" + publishId;
        this.redisTemplate.delete(loveUserCommentKey);

        return count;
    }

    /**
     * 查询单条动态
     *
     * @param publishId 动态id
     * @return 动态
     */
    public Movements queryMovementsById(String publishId) {
        //调用QuanZi中的方法进行实现
        Publish publish = this.quanZiApi.queryPublishById(publishId);
        //判断
        if (null == publish) {
            //查询失败
            return null;
        }
        //查询成功,填充数据
        List<Movements> movementsList = this.fillValueToMovements(Arrays.asList(publish));
        return movementsList.get(0);
    }

    /**
     * 动态信息数据的填充
     *
     * @param records 动态
     * @return 动态的完整信息
     */
    private List<Movements> fillValueToMovements(List<Publish> records) {
        //校验
        User user = UserThreadLocal.get();
        //创建集合用来保存所有的完整集合
        List<Movements> movementsList = new ArrayList<>();
        //创建集合用来保存所有的动态发布人的id--->Set去重
        Set<Long> userIds = new HashSet<>();
        for (Publish record : records) {
            Movements movements = new Movements();
            //填充主键信息
            movements.setId(record.getId().toHexString());
            //填充动态发布者的id
            movements.setUserId(record.getUserId());
            userIds.add(record.getUserId());
            String likeUserCommentKey = "QUANZI_COMMENT_LIKE_USER_" + user.getId() + "_" + movements.getId();
            //是否点赞
            movements.setHasLiked(this.redisTemplate.hasKey(likeUserCommentKey) ? 1 : 0);
            String likeCommentKey = "QUANZI_COMMENT_LIKE_" + movements.getId();
            String value = this.redisTemplate.opsForValue().get(likeCommentKey);
            //点赞数
            if (StringUtils.isNotEmpty(value)) {
                movements.setLikeCount(Integer.valueOf(value));
            } else {
                //点赞数
                movements.setLikeCount(0);
            }

            String loveUserCommentKey = "QUANZI_COMMENT_LOVE_USER_" + user.getId() + "_" + movements.getId();
            //是否喜欢
            movements.setHasLoved(this.redisTemplate.hasKey(loveUserCommentKey) ? 1 : 0);

            String loveCommentKey = "QUANZI_COMMENT_LOVE_" + movements.getId();
            String loveValue = this.redisTemplate.opsForValue().get(loveCommentKey);
            if (StringUtils.isNotEmpty(loveValue)) {
                //喜欢数
                movements.setLoveCount(Integer.valueOf(loveValue));
            } else {
                //喜欢数
                movements.setLoveCount(0);
            }
            //TODO 距离
            movements.setDistance("1.2公里");
            //TODO 评论数
            movements.setCommentCount(30);
            //发布时间，10分钟前
            movements.setCreateDate(RelativeDateFormat.format(new Date(record.getCreated())));
            //发布内容
            movements.setTextContent(record.getText());
            //发布图片
            movements.setImageContent(record.getMedias().toArray(new String[]{}));
            //填充
            movementsList.add(movements);
        }
        //设置动态发布人的信息查询条件
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(userInfoQueryWrapper);
        //填充动态发布人的信息
        for (Movements movements : movementsList) {
            for (UserInfo userInfo : userInfoList) {
                //发布动态的发布者的id要和用户信息中的用户id相等才能填充
                if (movements.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    //添加标签
                    movements.setTags(StringUtils.split(userInfo.getTags(), ','));
                    movements.setNickname(userInfo.getNickName());
                    movements.setGender(userInfo.getSex().name().toLowerCase());
                    //头像
                    movements.setAvatar(userInfo.getLogo());
                    movements.setAge(userInfo.getAge());
                    break;
                }
            }
        }
        return movementsList;
    }

    /**
     * 查询访客
     * @return 访客集合
     */
    public List<VisitorsVo> queryVisitorsList() {
        User user = UserThreadLocal.get();
        String redisKey = "visitor_date_" + user.getId();

        // 如果redis中存在上次查询的时间，就按照这个时间之后查询，如果没有就查询前5个
        List<Visitors> visitors = null;
        String value = this.redisTemplate.opsForValue().get(redisKey);
        if(StringUtils.isEmpty(value)){
            visitors = this.visitorsApi.topVisitor(user.getId(), 5);
        }else{
            visitors = this.visitorsApi.topVisitor(user.getId(), Long.valueOf(value));
        }

        if(CollectionUtils.isEmpty(visitors)){
            return Collections.emptyList();
        }

        //创建一个集合得到所有的数据
        List<Long> userIds = new ArrayList<>();
        for (Visitors visitor : visitors) {
            userIds.add(visitor.getVisitorUserId());
        }
        //设置查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);

        List<VisitorsVo> visitorsVoList = new ArrayList<>();

        for (Visitors visitor : visitors) {
            for (UserInfo userInfo : userInfoList) {
                if(visitor.getVisitorUserId().longValue() == userInfo.getUserId().longValue()){

                    VisitorsVo visitorsVo = new VisitorsVo();
                    visitorsVo.setAge(userInfo.getAge());
                    visitorsVo.setAvatar(userInfo.getLogo());
                    visitorsVo.setGender(userInfo.getSex().name().toLowerCase());
                    visitorsVo.setId(userInfo.getUserId());
                    visitorsVo.setNickname(userInfo.getNickName());
                    visitorsVo.setTags(StringUtils.split(userInfo.getTags(), ','));
                    visitorsVo.setFateValue(visitor.getScore().intValue());

                    visitorsVoList.add(visitorsVo);
                    break;
                }
            }
        }
        //更新时间
        this.redisTemplate.opsForValue().set(redisKey,String.valueOf(System.currentTimeMillis()));
        return visitorsVoList;
    }


    /**
     * 查询指定用户的相册表
     * @param userId 指定用户id
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult queryAlbumList(Long userId, Integer page, Integer pageSize) {
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        PageInfo<Publish> albumPageInfo = this.quanZiApi.queryAlbumList(userId, page, pageSize);
        List<Publish> records = albumPageInfo.getRecords();

        if(CollectionUtils.isEmpty(records)){
            return pageResult;
        }

        pageResult.setItems(this.fillValueToMovements(records));

        return pageResult;
    }
}
