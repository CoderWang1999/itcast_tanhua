package com.tanhua.server.controller;

import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.utils.Cache;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.NearUserVo;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.RecommendUserQueryParam;
import com.tanhua.server.vo.TodayBest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("tanhua")
@Slf4j
public class TodayBestController {

    @Autowired
    private TodayBestService todayBestService;

    /**
     * 查询今日佳人
     *
     * @return 佳人
     */
    @GetMapping("todayBest")
    public ResponseEntity<TodayBest> queryTodayBest() {
        try {
            //调用TodayBestService中的方法查询缘分值最高
            TodayBest todayBest = this.todayBestService.queryTodayBest();
            //判断
            if (null != todayBest) {
                //查询成功
                String msg = "查询今日佳人成功";
                log.info(msg);
                return ResponseEntity.ok(todayBest);
            }
        } catch (Exception e) {
            String msg = "查询今日佳人出错";
            log.error(msg, e);
        }
        //查询失败
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 查询推荐用户列表
     *
     * @param queryParam 分行参数
     * @return 分页后的结果
     */
    @GetMapping("recommendation")
    @Cache
    public ResponseEntity<PageResult> queryRecommendation(RecommendUserQueryParam queryParam) {
        try {
            //调用todayBestService的方法查询列表
            PageResult pageResult = this.todayBestService.queryRecommendation(queryParam);
            //判断
            if (null != pageResult) {
                //查询成功
                String msg = UserThreadLocal.get().getMobile() + "查询推荐用户成功";
                log.info(msg);
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "查询推荐用户失败";
            log.error(msg, e);
        }
        //查询失败
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 查询今日佳人详情
     *
     * @param userId 佳人id
     * @return 佳人
     */
    @GetMapping("{id}/personalInfo")
    public ResponseEntity<TodayBest> queryTodayBest(@PathVariable("id") Long userId) {
        try {
            TodayBest todayBest = this.todayBestService.queryTodayBest(userId);
            String msg = UserThreadLocal.get().getMobile() + "查询今日佳人详情成功";
            log.info(msg);
            return ResponseEntity.ok(todayBest);
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "查询今日佳人详情失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 查询陌生人问题
     *
     * @param userId 用户id
     * @return 问题的字符串
     */
    @GetMapping("strangerQuestions")
    public ResponseEntity<String> queryQuestion(@RequestParam("userId") Long userId) {
        try {
            String question = this.todayBestService.queryQuestion(userId);
            if (StringUtils.isNotEmpty(question)) {
                String msg = UserThreadLocal.get().getMobile() + "查询" + userId + "设置的问题成功";
                log.info(msg);
                return ResponseEntity.ok(question);
            }

        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "查询" + userId + "设置的问题失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 回复陌生人问题
     *
     * @return
     */
    @PostMapping("strangerQuestions")
    public ResponseEntity<Void> replyQuestion(@RequestBody Map<String, Object> param) {
        try {
            Long userId = Long.valueOf(param.get("userId").toString());
            String reply = param.get("reply").toString();
            Boolean result = this.todayBestService.replyQuestion(userId, reply);
            if (result) {
                String msg = "回复陌生人问题成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            String msg = "回复陌生人问题失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 搜附近
     *
     * @param gender
     * @param distance
     * @return
     */
    @GetMapping("search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(@RequestParam(value = "gender", required = false) String gender,
                                                          @RequestParam(value = "distance", defaultValue = "2000") String distance) {
        try {
            List<NearUserVo> list = this.todayBestService.queryNearUser(gender, distance);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 探花
     *
     * @return 佳人
     */
    @GetMapping("cards")
    public ResponseEntity<List<TodayBest>> queryCardsList() {
        try {
            List<TodayBest> list = this.todayBestService.queryCardsList();
            String msg = UserThreadLocal.get().getMobile() + "查询卡片列表集合成功";
            log.info(msg);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "查询卡片列表集合失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 喜欢
     *
     * @param likeUserId 喜欢人的id
     * @return 状态信息
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id") Long likeUserId) {
        try {
            boolean b = this.todayBestService.likeUser(likeUserId);
            if (b) {
                String msg = UserThreadLocal.get().getMobile() + "喜欢" + likeUserId + "成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "喜欢" + likeUserId + "失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 不喜欢
     *
     * @param likeUserId 喜欢人的id
     * @return 状态信息
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Void> disLikeUser(@PathVariable("id") Long likeUserId) {
        try {
            boolean bool=this.todayBestService.disLikeUser(likeUserId);
            if (bool){
                String msg=UserThreadLocal.get().getMobile()+"不喜欢"+likeUserId;
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            String msg=UserThreadLocal.get().getMobile()+"不喜欢"+likeUserId;
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
