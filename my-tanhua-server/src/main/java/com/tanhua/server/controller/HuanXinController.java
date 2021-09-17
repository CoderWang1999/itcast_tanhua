package com.tanhua.server.controller;

import com.tanhua.server.pojo.User;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.HuanXinUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("huanxin")
public class HuanXinController {

    /**
     * 得到环信用户
     * @return 环信用户
     */
    @GetMapping("user")
    public ResponseEntity<HuanXinUser> queryHuanXinUser(){
        User user = UserThreadLocal.get();
        HuanXinUser huanXinUser = new HuanXinUser();
        //得到环信用户名
        huanXinUser.setUsername(user.getId().toString());
        //设置环信密码
        huanXinUser.setPassword(DigestUtils.md5Hex(user.getId() + "_itcast_tanhua"));
        return ResponseEntity.ok(huanXinUser);
    }
}
