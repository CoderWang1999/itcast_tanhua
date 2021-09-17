package com.tanhua.server.controller;

import com.tanhua.server.service.MovementsService;
import com.tanhua.server.service.VideoMQService;
import com.tanhua.server.service.VideoService;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("smallVideos")
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private MovementsController movementsController;

    @Autowired
    private CommentsController commentsController;

    @Autowired
    private MovementsService movementsService;

    @Autowired
    private VideoMQService videoMQService;

    /**
     * 保存小视频
     *
     * @param picFile   封面图片
     * @param videoFile 视频文件
     * @return 状态信息
     */
    @PostMapping
    public ResponseEntity<Void> saveVideo(@RequestParam(value = "videoThumbnail", required = false) MultipartFile picFile,
                                          @RequestParam(value = "videoFile", required = false) MultipartFile videoFile) {
        try {
            //调用VideoService中的方法保存
            String id = this.videoService.saveVideo(picFile, videoFile);
            if (StringUtils.isNotEmpty(id)) {
                //成功
                String msg = UserThreadLocal.get().getMobile() + "保存小视频成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            //失败
            String msg = UserThreadLocal.get().getMobile() + "保存小视频失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 查询小视频列表
     *
     * @param page     页码
     * @param pageSize 页大小
     * @return 分页后的结果
     */
    @GetMapping
    public ResponseEntity<PageResult> queryVideoList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                     @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        try {
            if (page <= 0) {
                page = 1;
            }
            PageResult pageResult = this.videoService.queryVideoList(page, pageSize);
            if (null != pageResult) {
                //查询成功
                String msg=UserThreadLocal.get().getMobile()+"查询小视频成功";
                log.info(msg);
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            //查询失败
            String msg=UserThreadLocal.get().getMobile()+"查询小视频成功";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 视频点赞
     *
     * @param videoId 视频id
     * @return 数值
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Long> likeComment(@PathVariable("id") String videoId) {
        try {
            Long likeCount = this.movementsService.likeComment(videoId);
            if (likeCount != null) {
                this.videoMQService.likeVideoMsg(videoId);
                return ResponseEntity.ok(likeCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 取消点赞
     *
     * @param videoId 视频id
     * @return 数值
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Long> disLikeComment(@PathVariable("id") String videoId) {
        try {
            Long likeCount = this.movementsService.disLikeComment(videoId);
            if (null != likeCount) {
                this.videoMQService.disLikeVideoMsg(videoId);
                return ResponseEntity.ok(likeCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 评论列表
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<PageResult> queryCommentsList(@PathVariable("id") String videoId,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize) {
        return this.commentsController.queryCommentsList(videoId, page, pagesize);
    }
    /**
     * 提交评论
     *
     * @param param 参数
     * @param videoId 视频id
     * @return 状态信息
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> saveComments(@RequestBody Map<String, String> param,
                                             @PathVariable("id") String videoId) {
        param.put("movementId", videoId);
        ResponseEntity<Void> entity = this.commentsController.saveComments(param);

        if (entity.getStatusCode().is2xxSuccessful()) {
            //发送消息
            this.videoMQService.commentVideoMsg(videoId);
        }

        return entity;
    }

    /**
     * 视频评论点赞
     *
     * @param publishId 评论id
     * @return 数值
     */
    @PostMapping("/comments/{id}/like")
    public ResponseEntity<Long> commentsLikeComment(@PathVariable("id") String publishId) {
        try {
            Long likeCount = this.movementsService.likeComment(publishId);
            if (likeCount != null) {
                return ResponseEntity.ok(likeCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 视频评论取消点赞
     *
     * @param publishId 评论id
     * @return 数值
     */
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity<Long> disCommentsLikeComment(@PathVariable("id") String publishId) {
        try {
            Long likeCount = this.movementsService.disLikeComment(publishId);
            if (null != likeCount) {
                return ResponseEntity.ok(likeCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 视频用户关注
     * @param userId 用户id
     * @return 状态信息
     */
    @PostMapping("/{id}/userFocus")
    public ResponseEntity<Void> saveUserFocusComments(@PathVariable("id") Long userId) {
        try {
            Boolean bool = this.videoService.followUser(userId);
            if (bool) {
                //关注成功
                String msg=UserThreadLocal.get().getMobile()+"关注"+userId+"成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            //关注失败
            String msg=UserThreadLocal.get().getMobile()+"关注"+userId+"失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 取消视频用户关注
     */
    @PostMapping("/{id}/userUnFocus")
    public ResponseEntity<Void> saveUserUnFocusComments(@PathVariable("id") Long userId) {
        try {
            Boolean bool = this.videoService.disFollowUser(userId);
            if (bool) {
                //取消关注成功
                String msg=UserThreadLocal.get().getMobile()+"取消关注"+userId+"成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            //取消关注失败
            String msg=UserThreadLocal.get().getMobile()+"取消关注"+userId+"失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
