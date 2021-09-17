package com.tanhua.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ZJWzxy
 */
@RestController
@RequestMapping("users")
public class UsersController {

    @Autowired
    private UserInfoController userInfoController;

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @param token token
     * @return 状态码
     */
    @PostMapping("header")
    public ResponseEntity<Object> saveLogo(@RequestParam("headPhoto") MultipartFile file, @RequestHeader("Authorization") String token) {
        return this.userInfoController.saveUserLogo(file, token);
    }
}
