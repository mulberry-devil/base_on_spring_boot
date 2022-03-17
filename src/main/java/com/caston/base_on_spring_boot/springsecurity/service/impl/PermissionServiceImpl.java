package com.caston.base_on_spring_boot.springsecurity.service.impl;

import com.caston.base_on_spring_boot.springsecurity.entity.Permission;
import com.caston.base_on_spring_boot.springsecurity.mapper.PermissionMapper;
import com.caston.base_on_spring_boot.springsecurity.service.PermissionService;
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
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
