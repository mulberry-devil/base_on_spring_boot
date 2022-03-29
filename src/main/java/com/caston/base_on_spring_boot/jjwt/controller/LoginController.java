package com.caston.base_on_spring_boot.jjwt.controller;

import com.caston.base_on_spring_boot.jjwt.utils.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("结合spring boot使用jjwt")
@RequestMapping("/jjwt")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    public static ThreadLocal<String> login = new ThreadLocal<>();

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "path"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "path")
    })
    @GetMapping("/login/{userId}/{password}")
    public ResponseEntity login(@PathVariable String userId, @PathVariable String password) {
        String jwt = JWTUtil.generate(userId);
        log.info(jwt);
        HttpHeaders headers = new HttpHeaders();
        headers.add("admin-token", jwt);
        return new ResponseEntity("可将返回对象或者JSON放入此位置", headers, HttpStatus.OK);
    }

    @GetMapping("/success")
    public String success() {
        return login.get();
    }
}
