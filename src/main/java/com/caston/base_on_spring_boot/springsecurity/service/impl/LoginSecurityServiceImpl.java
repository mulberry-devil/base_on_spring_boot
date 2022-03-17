package com.caston.base_on_spring_boot.springsecurity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.base_on_spring_boot.springsecurity.entity.LoginTable;
import com.caston.base_on_spring_boot.springsecurity.entity.Permission;
import com.caston.base_on_spring_boot.springsecurity.entity.Role;
import com.caston.base_on_spring_boot.springsecurity.mapper.LoginTableMapper;
import com.caston.base_on_spring_boot.springsecurity.mapper.PermissionMapper;
import com.caston.base_on_spring_boot.springsecurity.mapper.RoleMapper;
import com.caston.base_on_spring_boot.springsecurity.service.LoginSecurityService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginSecurityServiceImpl implements LoginSecurityService {
    @Resource
    private LoginTableMapper loginTableMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginTable user = loginTableMapper.selectOne(new LambdaQueryWrapper<LoginTable>().eq(LoginTable::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        List<Role> roleList = roleMapper.findRoleListByUserId(user.getId());
        roleList.forEach(i -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + i.getRoleKeyword()));
            List<Permission> permissionList = permissionMapper.findPermissionByRole(i.getId());
            permissionList.forEach(j -> {
                authorities.add(new SimpleGrantedAuthority(j.getPermissionKeyword()));
            });
        });
        UserDetails userDetails = new User(username, user.getPassword(), authorities);
        return userDetails;
    }
}
