package com.tanhua.sso.controller;

import com.tanhua.sso.service.HuanXinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("user/huanxin")
@Slf4j
public class HuanXinController {

    @Autowired
    private HuanXinService huanXinService;

    /**
     * 添加联系人
     *
     * @param userId 用户id
     * @param friendId 好友id
     * @return 状态信息
     */
    @PostMapping("contacts/{owner_username}/{friend_username}")
    public ResponseEntity<Void> contactUsers(@PathVariable("owner_username") Long userId,
                                             @PathVariable("friend_username") Long friendId) {
        try {
            boolean result = this.huanXinService.contactUsers(userId, friendId);
            if (result) {
                String msg=userId+"添加"+friendId+"为好友到环信成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            String msg=userId+"添加"+friendId+"为好友到环信失败";
            log.info(msg,e);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 发送系统消息
     *
     * @param target
     * @param msg
     * @param type
     * @return
     */
    @PostMapping("messages")
    public ResponseEntity<Void> sendMsg(@RequestParam("target") String target,
                                        @RequestParam("msg") String msg,
                                        @RequestParam(value = "type", defaultValue = "txt") String type) {
        try {
            boolean result = this.huanXinService.sendMsg(target, type, msg);
            if (result) {
                String message="发送系统消息成功";
                log.info(message);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            String message="发送系统消息失败";
            log.info(message,e);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
