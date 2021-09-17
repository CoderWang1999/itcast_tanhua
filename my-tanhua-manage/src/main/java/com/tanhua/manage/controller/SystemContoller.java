package com.tanhua.manage.controller;

import com.tanhua.manage.pojo.Admin;
import com.tanhua.manage.service.SystemService;
import com.tanhua.manage.utils.Captcha;
import com.tanhua.manage.utils.NoAuthorization;
import com.tanhua.manage.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS.required;

@RestController
@RequestMapping("/management/system/users")
@Slf4j
public class SystemContoller {
    @Autowired
    private SystemService systemService;
    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> param) {
        try {

            //调用Service中的login方法进行登录
            String token = systemService.login(param);

            //判断是否登录成功
            if (token!=null){
                //成功

                Map<String, String> map = new HashMap<>();
                map.put("token", token);
                return ResponseEntity.ok(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //登录失败
      ErrorResult errorResult= ErrorResult.builder().errCode("000002").errMessage("登录失败").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    @PostMapping("profile")
    public ResponseEntity<Object> find(@RequestHeader(required = false, name = "Authorization") String token) {
        try {

            String newToken = token.replace("Bearer ", "");
            System.out.println(token);
            Admin admin = systemService.find(newToken);

            //判断是否登录成功
            if (admin!=null){
                //成功


                return ResponseEntity.ok(admin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //登录失败
        ErrorResult errorResult= ErrorResult.builder().errCode("000002").errMessage("登录失败").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    /**
     * 功能描述: 生成验证码图片
     * @param uuid              浏览器随机生成
     * @param response          响应体
     * @param request           请求体
     * @Return: void            无返回参数
     * @throws:                 异常
     */

    @GetMapping("verification")
    public void verification(@RequestParam(name="uuid") String uuid, HttpServletResponse response, HttpServletRequest request){
        //响应头
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        Captcha captcha= new Captcha();

        //生成图片
        String code= captcha.getRandcode(request,response);
        //调用adminService里面的saveVerification方法,传入uuid和验证码
        systemService.saveVerification(uuid,code);
    }

    /**
     * 登出
     *
     * @param token         令牌
     */
    @PostMapping("logout")
    private void logout(@RequestHeader("Authorization") String token){
        String newToken = token.replace("Bearer ", "");
        this.systemService.removeToken(newToken);
    }
}
