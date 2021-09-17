package com.tanhua.sso.controller;


import com.tanhua.sso.service.SmsService;
import com.tanhua.sso.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ZJWzxy
 * @date 2021/04/07
 */
@RestController
@RequestMapping("user")
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送短信验证码接口
     *
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("login")
    public ResponseEntity<ErrorResult> sendCheckCode(@RequestBody Map<String, String> param) {
        ErrorResult errorResult = null;
        //从前端得到输入的手机号
        String phone = param.get("phone");
        try {
            //调用SmsService中的sendCheckCode方法的的得到传入手机号的结果
            errorResult = this.smsService.sendCheckCode(phone);
            if (null == errorResult) {
                //发送验证码成功
                String msg="验证码发送成功";
                log.info(msg);
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            //失败
            log.error("发送验证码失败~ phone" + phone, e);
            errorResult=ErrorResult.builder().errCode("000002").errMessage("短信验证码发送失败!").build();
        }
        //向前端返回失败结果信息
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
}
