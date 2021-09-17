package com.tanhua.server.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.UsersApi;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.Announcement;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Contacts;
import com.tanhua.server.vo.MessageAnnouncement;
import com.tanhua.server.vo.MessageLike;
import com.tanhua.server.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ZJWzxy
 */
@Service
public class IMService {

    @Reference(version = "1.0.0")
    private UsersApi usersApi;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tanhua.sso.url}")
    private String url;

    @Autowired
    private UserInfoService userInfoService;

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private  AnnouncementService announcementService;


    /**
     * 添加好友
     *
     * @param userId 好友id
     */
    public boolean contactUser(Long userId) {
        User user = UserThreadLocal.get();

        Users users = new Users();
        users.setUserId(user.getId());
        users.setFriendId(userId);

        String id = this.usersApi.saveUsers(users);
        if (StringUtils.isNotEmpty(id)) {
            //注册好友关系到环信
            String targetUrl = url + "/user/huanxin/contacts/" + users.getUserId() + "/" + users.getFriendId();
            ResponseEntity<Void> responseEntity = this.restTemplate.postForEntity(targetUrl, null, Void.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * 查询联系人列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @param keyword  关键字
     * @return 分页结果
     */
    public PageResult queryContactsList(Integer page, Integer pageSize, String keyword) {
        //校验
        User user = UserThreadLocal.get();
        List<Users> usersList = null;
        //判断keyword是否为空
        if (StringUtils.isNotEmpty(keyword)) {
            //不为空
            //调用UsersApi查询所有的联系人---根据关键字进行查询
            usersList = this.usersApi.queryAllUsersList(user.getId());
        } else {
            //为空
            PageInfo<Users> usersPageInfo = this.usersApi.queryUsersList(user.getId(), page, pageSize);
            usersList = usersPageInfo.getRecords();
        }
        //得到所有的联系人id
        Set<Long> userIds = new HashSet<>();
        for (Users users : usersList) {
            userIds.add(users.getFriendId());
        }
        //通过id查询数据库进行信息填充
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("nick_name", keyword);
        }
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        List<Contacts> contactsList = new ArrayList<>();
        //填充信息
        for (UserInfo userInfo : userInfoList) {
            Contacts contacts = new Contacts();
            contacts.setAge(userInfo.getAge());
            contacts.setAvatar(userInfo.getLogo());
            contacts.setGender(userInfo.getSex().name().toLowerCase());
            contacts.setNickname(userInfo.getNickName());
            contacts.setUserId(String.valueOf(userInfo.getUserId()));
            contacts.setCity(StringUtils.substringBefore(userInfo.getCity(), "-"));
            contactsList.add(contacts);
            break;
        }
        //设置分页信息
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPagesize(pageSize);
        pageResult.setItems(contactsList);
        return pageResult;
    }

    /**
     * 查询点赞列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult queryMessageLikeList(Integer page, Integer pageSize) {
        return this.messageCommentList(1, page, pageSize);
    }

    /**
     * 查询评论列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult queryMessageCommentList(Integer page, Integer pageSize) {
        return this.messageCommentList(2, page, pageSize);
    }

    /**
     * 查询喜欢列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult queryMessageLoveList(Integer page, Integer pageSize) {
        return this.messageCommentList(3, page, pageSize);
    }



    /**
     * 查询公共列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @param type  列表类型---1.点赞 2.评论 3.喜欢
     * @return 分页结果
     */
    private PageResult messageCommentList(Integer type,Integer page, Integer pageSize) {
        //校验
        User user = UserThreadLocal.get();
        //调用QuanZiApi中的方法查询
        PageInfo<Comment> pageInfo = this.quanZiApi.queryCommentListByUser(user.getId(), type, page, pageSize);
        PageResult pageResult=new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        pageResult.setCounts(0);
        pageResult.setPages(0);
        //拿到所有的数据
        List<Comment> records = pageInfo.getRecords();
        //拿到所有的id
        Set<Long> userIds=new HashSet<>();
        for (Comment comment : records) {
            userIds.add(comment.getUserId());
        }
        //设置查询条件
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("user_id",userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        //创建一个集合用来收集所有的点赞
        List<MessageLike> messageLikeList = new ArrayList<>();
        for (Comment record : records) {
            for (UserInfo userInfo : userInfoList) {
                if(userInfo.getUserId().longValue() == record.getUserId().longValue()){

                    MessageLike messageLike = new MessageLike();
                    messageLike.setId(record.getId().toHexString());
                    messageLike.setAvatar(userInfo.getLogo());
                    messageLike.setNickname(userInfo.getNickName());
                    messageLike.setCreateDate(new DateTime(record.getCreated()).toString("yyyy-MM-dd HH:mm"));

                    messageLikeList.add(messageLike);
                    break;
                }
            }
        }
        pageResult.setItems(messageLikeList);
        return pageResult;
    }


    /**
     * 查询公告列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult queryMessageAnnouncementList(Integer page, Integer pageSize) {
        IPage<Announcement> announcementPage = this.announcementService.queryList(page, pageSize);

        List<MessageAnnouncement> messageAnnouncementList = new ArrayList<>();

        for (Announcement record : announcementPage.getRecords()) {
            MessageAnnouncement messageAnnouncement = new MessageAnnouncement();
            messageAnnouncement.setId(record.getId().toString());
            messageAnnouncement.setTitle(record.getTitle());
            messageAnnouncement.setDescription(record.getDescription());
            messageAnnouncement.setCreateDate(new DateTime(record.getCreated()).toString("yyyy-MM-dd HH:mm"));

            messageAnnouncementList.add(messageAnnouncement);
        }

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        pageResult.setPagesize(pageSize);
        pageResult.setItems(messageAnnouncementList);

        return pageResult;
    }

}
