package com.tanhua.server.controller;

import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.server.service.MovementsService;
import com.tanhua.server.service.QuanziMQService;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.Movements;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.VisitorsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author ZJWzxy
 * @date 2021/04/15
 */
@RestController
@RequestMapping("movements")
@Slf4j
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    @Autowired
    private QuanziMQService quanziMQService;

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
    @PostMapping()
    public ResponseEntity<Void> savePublish(@RequestParam(value = "textContent", required = false) String textContent,
                                            @RequestParam(value = "location", required = false) String location,
                                            @RequestParam(value = "longitude", required = false) String longitude,
                                            @RequestParam(value = "latitude", required = false) String latitude,
                                             @RequestParam(value = "imageContent", required = false) MultipartFile[] multipartFile) {

        try {
            //调用MovementsService层的发布动态方法
            String publishId = this.movementsService.savePublish(textContent, location, longitude, latitude, multipartFile);
            //判断是否发送成功
            if (StringUtils.isNotEmpty(publishId)) {
                //成功,打印在控制台
                String msg = UserThreadLocal.get().getMobile() + "动态发布成功";
                log.info(msg);
                //发送消息
                this.quanziMQService.publishMsg(publishId);
                return ResponseEntity.ok(null);

            }
        } catch (Exception e) {
            //失败
            String msg = UserThreadLocal.get().getMobile() + "发布动态失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询好友动态
     *
     * @param page     页码
     * @param pageSize 每页显示条数
     * @return 分页后的数据结果
     */
    @GetMapping()
    public ResponseEntity<PageResult> queryPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        //调用MovementsService中的方法查询好友动态
        try {
            PageResult pageResult = this.movementsService.queryUserPublishList(page, pageSize);
            if (null != pageResult) {
                //查询成功,打印到控制台
                String msg = UserThreadLocal.get().getMobile() + "查询好友动态成功";
                log.info(msg);
                log.info(pageResult.getItems().toString());
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "查询好友动态失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 查询推荐动态
     */
    @GetMapping("recommend")
    public ResponseEntity<PageResult> queryRecommendPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        try {
            //调用MovementsService中的方法查询推荐动态
            PageResult pageResult = this.movementsService.queryRecommendPublishList(page, pageSize);
            if (null != pageResult) {
                //查询成功
                String msg = "查询推荐列表成功";
                log.info(msg);
                log.info(pageResult.getItems().toString());
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            //查询失败
            String msg = "查询推荐列表失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 点赞
     *
     * @param publishId 动态id
     * @return 点赞数
     */
    @GetMapping("/{id}/like")
    public ResponseEntity<Long> likeComment(@PathVariable("id") String publishId) {
        //调用MovementsService中的方法查询查询点赞数
        Long count = this.movementsService.likeComment(publishId);
        try {
            if (null != count) {
                //查询成功
                String msg = UserThreadLocal.get().getMobile() + "点赞" + publishId + "成功";
                log.info(msg);
                //发送消息
                this.quanziMQService.likePublishMsg(publishId);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "点赞失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 取消点赞
     *
     * @param publishId 动态id
     * @return 点赞数
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Long> disLikeComment(@PathVariable("id") String publishId) {
        //调用MovementsService中的方法查询查询点赞数
        Long count = this.movementsService.disLikeComment(publishId);
        try {
            if (null != count) {
                //取消成功
                String msg = UserThreadLocal.get().getMobile() + "取消点赞" + publishId + "成功";
                log.info(msg);
                //发送消息
                this.quanziMQService.disLikePublishMsg(publishId);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "取消点赞" + publishId + "失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 喜欢
     *
     * @param publishId 动态id
     * @return 喜欢数量
     */
    @GetMapping("/{id}/love")
    public ResponseEntity<Long> loveComment(@PathVariable("id") String publishId) {

        try {
            //调用MovementsService中的方法来喜欢
            Long count = this.movementsService.loveComment(publishId);
            if (null != count) {
                //查询成功
                String msg = UserThreadLocal.get().getMobile() + "喜欢" + publishId + "成功";
                log.info(msg);
                //发送消息
                this.quanziMQService.lovePublishMsg(publishId);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "喜欢" + publishId + "失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 取消喜欢
     *
     * @param publishId 动态id
     * @return 取消喜欢的数量
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity<Long> unLoveComment(@PathVariable("id") String publishId) {
        try {
            //调用MovementsService中的方法来取消喜欢
            Long count = this.movementsService.unLoveComment(publishId);
            if (null != count) {
                //取消成功
                String msg = UserThreadLocal.get().getMobile() + "取消喜欢" + publishId + "成功";
                log.info(msg);
                //发送消息
                this.quanziMQService.disLovePublishMsg(publishId);
                return ResponseEntity.ok(count);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "取消喜欢" + publishId + "失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    /**
     * 查询单条动态
     *
     * @param publishId 动态id
     * @return 动态
     */
    @GetMapping("{id}")
    public ResponseEntity<Movements> queryMovementsById(@PathVariable("id") String publishId) {
        try {
            //调用MovementsService的方法查询单条动态
            Movements movements = this.movementsService.queryMovementsById(publishId);
            //判断
            if (null != movements) {
                //查询成功
                String msg = UserThreadLocal.get().getMobile() + "查询" + publishId + "成功";
                log.info(msg);
                //发送消息
                this.quanziMQService.queryPublishMsg(publishId);
                return ResponseEntity.ok(movements);
            }
        } catch (Exception e) {
            //查询失败
            String msg = UserThreadLocal.get().getMobile() + "查询" + publishId + "失败";
            log.error(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 谁看过我
     *
     * @return 访客集合
     */
    @GetMapping("visitors")
    public ResponseEntity<List<VisitorsVo>> queryVisitorsList(){
        try {
            List<VisitorsVo> list = this.movementsService.queryVisitorsList();
            String msg=UserThreadLocal.get().getMobile()+"查询访客成功";
            log.info(msg);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            String msg=UserThreadLocal.get().getMobile()+"查询访客失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 查询的所有动态
     *
     * @return 分页结果
     */
    @GetMapping("all")
    public ResponseEntity<PageResult> queryAlbumList(@RequestParam(value = "page", defaultValue = "1") Integer page,@RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize, @RequestParam(value = "userId") Long userId) {
        try {
            PageResult pageResult = this.movementsService.queryAlbumList(userId, page, pageSize);
            if (null!=pageResult){
                String msg=UserThreadLocal.get().getMobile()+"查询"+userId+"的所有动态成功";
                log.info(msg);
                return ResponseEntity.ok(pageResult);
            }

        } catch (Exception e) {
            String msg=UserThreadLocal.get().getMobile()+"查询"+userId+"的所有动态失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
