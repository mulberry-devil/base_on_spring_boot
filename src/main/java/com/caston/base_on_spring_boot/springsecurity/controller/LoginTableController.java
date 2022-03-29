package com.caston.base_on_spring_boot.springsecurity.controller;


import com.caston.base_on_spring_boot.springsecurity.entity.LoginTable;
import com.caston.base_on_spring_boot.springsecurity.service.LoginTableService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author caston
 * @since 2022-03-17
 */
@RestController
@RequestMapping("/loginTable")
public class LoginTableController {

    @Resource
    private LoginTableService loginTableService;

    @GetMapping("/findAll")
//    @PreAuthorize("hasAuthority('USER_FINDALL')") // 配置权限，对应权限列表中的名
    public List<LoginTable> findAll() {
        return loginTableService.list();
    }

    @GetMapping("/findAge")
//    @PreAuthorize("hasRole('ROLE_ADMIN')") // 配置角色，对应权限列表中的名
    public List<LoginTable> findAge() {
        return loginTableService.list();
    }
}

