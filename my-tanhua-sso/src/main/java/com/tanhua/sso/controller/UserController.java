package com.tanhua.sso.controller;


import com.tanhua.sso.pojo.User;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZJWzxy
 * @date 2021/04/07
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;



    /**
     *登录操作
     * @param param 手机号 验证码
     * @return  返回值
     */
    @PostMapping("loginVerification")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> param) {
        try {
            //得到手机号
            String phone = param.get("phone");
            //得到验证码
            String code = param.get("verificationCode");
            //调用UserService中的login方法进行登录
            Map<String,Object> data = this.userService.login(phone, code);
            //判断是否登录成功
            if (data!=null){
                //成功
                String msg=param.get("phone")+"---->登录成功";
                log.info(msg);
                return ResponseEntity.ok(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //登录失败
        ErrorResult errorResult=ErrorResult.builder().errCode("000002").errMessage("登录失败").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    /**
     * 校验token，根据token查询用户数据
     *
     * @param token 用户数据
     * @return user or null
     */
    @GetMapping("{token}")
    public User queryUserToken(@PathVariable("token") String token) {
        return this.userService.queryUserToken(token);
    }
}
