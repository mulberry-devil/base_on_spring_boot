package com.caston.base_on_spring_boot.springsecurity.service.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caston.base_on_spring_boot.springsecurity.entity.LoginTable;
import com.caston.base_on_spring_boot.springsecurity.entity.Permission;
import com.caston.base_on_spring_boot.springsecurity.entity.Role;
import com.caston.base_on_spring_boot.springsecurity.mapper.LoginTableMapper;
import com.caston.base_on_spring_boot.springsecurity.mapper.PermissionMapper;
import com.caston.base_on_spring_boot.springsecurity.mapper.RoleMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginSecurityService implements UserDetailsService {
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
            authorities.add(new MySimpleGrantedAuthority("ROLE_" + i.getRoleKeyword()));
            List<Permission> permissionList = permissionMapper.findPermissionByRole(i.getId());
            permissionList.forEach(j -> {
                authorities.add(new MySimpleGrantedAuthority(j.getPermissionKeyword(), j.getPath()));
            });
        });
        // 数据库密码应为经过 new BCryptPasswordEncoder().encode("密码") 编译后的
        UserDetails userDetails = new User(username, user.getPassword(), authorities);
        return userDetails;
    }
}
