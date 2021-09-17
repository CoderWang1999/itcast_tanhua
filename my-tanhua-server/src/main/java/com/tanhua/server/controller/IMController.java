package com.tanhua.server.controller;

import com.tanhua.server.service.IMService;
import com.tanhua.server.utils.NoAuthorization;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("messages")
@Slf4j
public class IMController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMController.class);

    @Autowired
    private IMService imService;

    /**
     * 添加好友到本地
     *
     * @param param 好友id
     * @return 状态码
     */
    @PostMapping("contacts")
    public ResponseEntity<Void> contactUser(@RequestBody Map<String, Object> param) {
        try {
            Long userId = Long.valueOf(param.get("userId").toString());
            boolean result = this.imService.contactUser(userId);
            if (result) {
                String msg= UserThreadLocal.get().getMobile()+"添加好友到本地成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            LOGGER.error("添加联系人失败! param = " + param, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询联系人列表
     *
     * @param page 页码
     * @param pageSize 页大小
     * @param keyword 关键字
     * @return 分页后的结果
     */
    @GetMapping("contacts")
    public ResponseEntity<PageResult> queryContactsList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                                        @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            PageResult pageResult = this.imService.queryContactsList(page, pageSize, keyword);
            if (null!=pageResult){
                //查询成功
                String msg=UserThreadLocal.get().getMobile()+"查询好友列表成功";
                log.info(msg,pageResult);
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            //查询成功
            String msg=UserThreadLocal.get().getMobile()+"查询好友列表失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询点赞列表
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("likes")
    public ResponseEntity<PageResult> queryMessageLikeList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize){
        PageResult pageResult = this.imService.queryMessageLikeList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询评论列表
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("comments")
    public ResponseEntity<PageResult> queryMessageCommentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.imService.queryMessageCommentList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询喜欢列表
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("loves")
    public ResponseEntity<PageResult> queryMessageLoveList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        PageResult pageResult = this.imService.queryMessageLoveList(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 查询公告列表
     *
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("announcements")
    @NoAuthorization
    public ResponseEntity<PageResult> queryMessageAnnouncementList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                   @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        try {
            PageResult pageResult = this.imService.queryMessageAnnouncementList(page, pageSize);
            if (null!=pageResult){
                //查询成功
                String msg=UserThreadLocal.get().getMobile()+"查询公告列表成功";
                log.info(msg);
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            //查询失败
            String msg=UserThreadLocal.get().getMobile()+"查询公告列表失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
