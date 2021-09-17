package com.tanhua.server.controller;

import com.tanhua.server.service.CommentsService;
import com.tanhua.server.service.MovementsService;
import com.tanhua.server.service.QuanziMQService;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("comments")
@Slf4j
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private MovementsService movementsService;

    @Autowired
    private QuanziMQService quanziMQService;

    /**
     * 查询评论列表
     *
     * @param publishId 动态id
     * @param page      页码
     * @param pageSize  页大小
     * @return 分页后的结果
     */
    @GetMapping
    public ResponseEntity<PageResult> queryCommentsList(@RequestParam("movementId") String publishId,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        try {
            //调用CommentsService中的方法来查询
            PageResult pageResult = this.commentsService.queryCommentsList(publishId, page, pageSize);
            if (null != pageResult) {
                //查询成功
                String msg = UserThreadLocal.get().getMobile() + "查询评论列表成功";
                log.info(msg);
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            //查询失败
            String msg = UserThreadLocal.get().getMobile() + "查询评论列表失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 发表评论
     *
     * @param param 包含动态编号和评论内容
     * @return 状态信息
     */
    @PostMapping
    public ResponseEntity<Void> saveComments(@RequestBody Map<String, String> param) {
        try {
            String publishId = param.get("movementId");
            String content = param.get("comment");
            Boolean bool = this.commentsService.saveComments(publishId, content);
            if (bool) {
                //发表成功
                String msg = UserThreadLocal.get().getMobile() + "发表评论成功";
                log.info(msg);
                this.quanziMQService.commentPublishMsg(publishId);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            //发表失败
            String msg = UserThreadLocal.get().getMobile() + "发表评论失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 点赞评论
     *
     * @param publishId 评论id
     * @return 结果
     */
    @GetMapping("/{id}/like")
    public ResponseEntity<Long> likeComment(@PathVariable("id") String publishId) {
        try {
            Long count = this.movementsService.likeComment(publishId);
            if (null != count) {
                //点赞成功
                String msg = UserThreadLocal.get().getMobile() + "点赞评论成功";
                log.info(msg);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            //点赞成功
            String msg = UserThreadLocal.get().getMobile() + "点赞评论失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 取消点赞评论
     *
     * @param publishId 评论id
     * @return 结果
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Long> disLikeComment(@PathVariable("id") String publishId) {
        try {
            Long count = this.movementsService.disLikeComment(publishId);
            if (null != count) {
                //取消点赞成功
                String msg = UserThreadLocal.get().getMobile() + "取消点赞评论成功";
                log.info(msg);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            //取消点赞失败
            String msg = UserThreadLocal.get().getMobile() + "取消点赞评论失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

