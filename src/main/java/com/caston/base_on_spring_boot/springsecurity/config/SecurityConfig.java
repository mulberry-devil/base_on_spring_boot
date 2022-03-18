package com.caston.base_on_spring_boot.springsecurity.config;

import com.caston.base_on_spring_boot.springsecurity.service.security.LoginSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启注解配置权限
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private LoginSecurityService loginSecurityService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.userDetailsService(loginSecurityService); // 自定义认证对象，单个时可写可不写，一般是多个service时才写
        http.authorizeRequests() // 开启登录配置
                //  .antMatchers("/loginTable/findAll").hasRole("admin") // 访问接口授权，此例说明需要角色为admin
                .antMatchers("/login").permitAll()
                .anyRequest().access("@authService.auth(request,authentication)") // 自定义service 来实现实时的权限认证，@后可以使用bean中的任何对象，此参数与类中的方法参数名一样
//                .anyRequest().authenticated() // 其他所有请求，只需要登录即可，在使用数据库配置权限时需要注释掉
                .and().formLogin()
                //  .loginPage("/login.html") // 自定义登录界面
                .loginProcessingUrl("/login") // 登录处理接口
                .usernameParameter("username") // 定义登陆时的用户名的key，默认为username
                .passwordParameter("password") // 定义登陆时的密码的key，默认为password
                //  .successForwardUrl("") // 登录成功跳转url，为post请求
                //  .defaultSuccessUrl("") // 登录成功跳转url，为get请求
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        System.out.println("1111111111111111111111111111111111111111111111");
                    }
                }) // 登录成功处理器
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        System.out.println("22222222222222222222222222222222222222222");
                    }
                }) // 登录失败处理器
                .permitAll()
                .and().logout()
                .logoutUrl("/logout") // 退出登录接口
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        System.out.println("333333333333333333333333333333333");
                    }
                }) // 退出登录成功处理器
                .permitAll()
                .and().httpBasic().and().csrf().disable();
    }

    /*
    忽略swagger
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger-ui/**")
                .antMatchers("/v3/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/ehcache/**");
    }
}
