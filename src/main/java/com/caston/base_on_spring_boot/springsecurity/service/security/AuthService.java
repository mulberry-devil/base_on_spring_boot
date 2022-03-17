package com.caston.base_on_spring_boot.springsecurity.service.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Service
public class AuthService {
    /**
     * 自定义权限授权
     *
     * @param request
     * @param authentication
     * @return true为放行，false代表拦截
     */
    public boolean auth(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        // 没有登录时为空或者为匿名状态
        if (principal == null || "anonymousUser".equals(principal)) {
            return false;
        }
        UserDetails userDetails = (UserDetails) principal;
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            MySimpleGrantedAuthority mySimpleGrantedAuthority = (MySimpleGrantedAuthority) authority;
            String path = mySimpleGrantedAuthority.getPath();
            String[] split = StringUtils.split(request.getRequestURI(), "?");
            if (split[0].equals(path)) {
                return true;
            }
        }
        return false;
    }
}
