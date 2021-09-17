/*
package com.tanhua.manage.config;

import com.tanhua.manage.service.SystemService;
import com.tanhua.manage.utils.NoAuthorization;
import com.tanhua.manage.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tanhua.manage.pojo.Admin;
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private SystemService systemService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (method.equals("OPTIONS")) {
            //跳过跨域中的OPTIONS请求
            return true;
        }
        //判断，请求的方法是否包含了 NoAuthorization ，如果包含了，就不需要做处理
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NoAuthorization annotation = handlerMethod.getMethod().getAnnotation(NoAuthorization.class);
            if (annotation != null) {
                return true;
            }
        }

        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");
        Admin admin = systemService.find(token);
        if (null == admin) {
            response.setStatus(401); //无权限
            return false;
        }
        // 存储到当前的线程中
        UserThreadLocal.set(admin);
        return true;
    }
}
*/
