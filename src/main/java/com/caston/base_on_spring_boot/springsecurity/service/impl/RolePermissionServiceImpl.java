package com.caston.base_on_spring_boot.springsecurity.service.impl;

import com.caston.base_on_spring_boot.springsecurity.entity.RolePermission;
import com.caston.base_on_spring_boot.springsecurity.mapper.RolePermissionMapper;
import com.caston.base_on_spring_boot.springsecurity.service.RolePermissionService;
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
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
