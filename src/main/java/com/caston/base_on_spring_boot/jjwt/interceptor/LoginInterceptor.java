package com.caston.base_on_spring_boot.jjwt.interceptor;

import com.caston.base_on_spring_boot.jjwt.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.caston.base_on_spring_boot.jjwt.controller.LoginController.login;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = request.getHeader("admin-token");
        if (!StringUtils.hasLength(jwt)){
            return false;
        }
        Claims claim = JWTUtil.parse(jwt);
        if (claim==null){
            return false;
        }
        String uid = claim.get("UID").toString();
        // 保存至线程变量中
        login.set(uid);
        // 刷新token，避免使用过程中失效
        jwt = JWTUtil.generate(uid);
        response.setHeader("admin-token",jwt);
        return true;
    }
}
