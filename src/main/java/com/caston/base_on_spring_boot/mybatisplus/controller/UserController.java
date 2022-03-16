package com.caston.base_on_spring_boot.mybatisplus.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caston.base_on_spring_boot.mybatisplus.entity.User;
import com.caston.base_on_spring_boot.mybatisplus.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @since 2022-03-15
 */
@RestController
@RequestMapping("/user")
@Api("MybatisPlus分页")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/findPage")
    @ApiOperation("MybatisPlus分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "条数", paramType = "query")
    })
    public Page<User> findPage(int current, int pageSize) {
        return userService.findPage(current, pageSize);
    }

    @GetMapping("/findAll")
    public List<User> findAll() {
        List<User> users = userService.list();
        return users;
    }

    @PostMapping("/save")
    public void save(User user) {
        userService.save(user);
    }

    @GetMapping("/queryByName")
    public User queryByName(String name) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getName, name));
        return user;
    }
}

