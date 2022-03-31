package com.caston.base_on_spring_boot.mybatisplus.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caston.base_on_spring_boot.mybatisplus.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author caston
 * @since 2022-03-15
 */
public interface UserService extends IService<User> {
    public Page<User> findPage(int current, int pageSize);
}
