package com.caston.base_on_spring_boot.springsecurity.service.impl;

import com.caston.base_on_spring_boot.springsecurity.entity.UserRole;
import com.caston.base_on_spring_boot.springsecurity.mapper.UserRoleMapper;
import com.caston.base_on_spring_boot.springsecurity.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-03-17
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
