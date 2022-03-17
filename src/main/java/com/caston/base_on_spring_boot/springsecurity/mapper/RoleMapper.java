package com.caston.base_on_spring_boot.springsecurity.mapper;

import com.caston.base_on_spring_boot.springsecurity.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author caston
 * @since 2022-03-17
 */
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> findRoleListByUserId(Long userId);
}
