package com.caston.base_on_spring_boot.springsecurity.service.impl;

import com.caston.base_on_spring_boot.springsecurity.entity.Role;
import com.caston.base_on_spring_boot.springsecurity.mapper.RoleMapper;
import com.caston.base_on_spring_boot.springsecurity.service.RoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
