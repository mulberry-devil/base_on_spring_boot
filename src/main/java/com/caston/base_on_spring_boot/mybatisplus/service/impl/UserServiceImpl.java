package com.caston.base_on_spring_boot.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caston.base_on_spring_boot.mybatisplus.entity.User;
import com.caston.base_on_spring_boot.mybatisplus.mapper.UserMapper;
import com.caston.base_on_spring_boot.mybatisplus.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-03-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    public Page<User> findPage(int current, int pageSize) {
        Page<User> page = new Page<>(current, pageSize);
        Page<User> userPage = userMapper.selectPage(page, new QueryWrapper<>());
        return userPage;
    }
}
