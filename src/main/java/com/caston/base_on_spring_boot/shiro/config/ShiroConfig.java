package com.caston.base_on_spring_boot.shiro.config;

import com.caston.base_on_spring_boot.shiro.filter.ShiroFilter;
import com.caston.base_on_spring_boot.shiro.realm.AccountRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    @Bean
    public AccountRealm accountRealm() {
        // 注入realm
        return new AccountRealm();
    }

    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("accountRealm") AccountRealm accountRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        // 创建MD5加密对象
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");
        matcher.setHashIterations(2);
        // 将加密对象设置进realm中
        accountRealm.setCredentialsMatcher(matcher);
        // 将自定义realm放入manager
        manager.setRealm(accountRealm);
        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 将前面的manager放入工厂中
        factoryBean.setSecurityManager(defaultWebSecurityManager);
        // 自定义权限过滤器
        LinkedHashMap<String, Filter> mapFilter = new LinkedHashMap<>();
        mapFilter.put("rolesOr", new ShiroFilter());
        factoryBean.setFilters(mapFilter);
        HashMap<String, String> map = new HashMap<>();
        map.put("/account/login/**", "anon");// 不用认证就能进入
        map.put("/swagger-ui/**", "anon");
        map.put("/swagger-resources/**", "anon");
        map.put("/v3/**", "anon");
        map.put("/webjars/**", "anon");
        map.put("/account/test/**", "rolesOr[manager,user]");// 使用上面自定义的过滤器
        map.put("/**", "authc");// 需要认证才能进入
        //  map.put("/account/perms/**","perms[manager]");// 配置什么权限能访问的
        //  map.put("/account/roles/**","roles[admin]");// 配置什么角色能访问的
        factoryBean.setFilterChainDefinitionMap(map);
        // 设置登录页面
        factoryBean.setLoginUrl("/account/reLogin");
        // 设置未授权页面
        factoryBean.setUnauthorizedUrl("/account/unauthc");
        return factoryBean;
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions)
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     *
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
