package com.caston.base_on_spring_boot.springsecurity.mapper;

import com.caston.base_on_spring_boot.springsecurity.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author caston
 * @since 2022-03-17
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    List<Permission> findPermissionByRole(Integer roleId);
}
