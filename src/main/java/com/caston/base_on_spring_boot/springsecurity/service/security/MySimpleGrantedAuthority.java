package com.caston.base_on_spring_boot.springsecurity.service.security;

import org.springframework.security.core.GrantedAuthority;

public class MySimpleGrantedAuthority implements GrantedAuthority {

    private String authority;

    public String getPath() {
        return path;
    }

    private String path;

    public MySimpleGrantedAuthority(String authority) {
        this.authority = authority;
    }

    public MySimpleGrantedAuthority(String authority, String path) {
        this.authority = authority;
        this.path = path;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
