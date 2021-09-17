package com.tanhua.sso.controller;

import com.tanhua.sso.service.UserInfoService;
import com.tanhua.sso.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author ZJWzxy
 * @date 2021/04/09
 */
@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 新登录用户需要完善个人基本信息
     *
     * @param param 用户信息
     * @param token true or false
     * @return errCode and msg
     */
    @PostMapping("loginReginfo")
    public ResponseEntity<Object> saveUserInfo(@RequestBody Map<String, String> param, @RequestHeader("Authorization") String token) {
        try {
            //调用UserInfoService中的saveUserInfo的方法保存用户信息
            Boolean bool = this.userInfoService.saveUserInfo(param, token);
            if (bool) {
                //保存成功
                return ResponseEntity.ok(null);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        //保存失败
        ErrorResult errorResult = ErrorResult.builder().errCode("000001").errMessage("保存用户信息失败").build();
        //向前端返回响应信息
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    /**
     * 上传用户头像
     *
     * @param file  信息
     * @param token true or false
     * @return
     */
    @PostMapping("loginReginfo/head")
    public ResponseEntity<Object> saveUserLogo(@RequestParam("headPhoto") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            //调用UserInfoService中的saveUserLogo的方法保存用户信息
            Boolean bool = this.userInfoService.saveUserLogo(file, token);
            if (bool){
                //保存成功
                return ResponseEntity.ok(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //保存失败
        ErrorResult errorResult=ErrorResult.builder().errCode("000001").errMessage("保存用户logo失败").build();
        //向前端返回信息
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
}
