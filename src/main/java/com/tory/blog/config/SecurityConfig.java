package com.tory.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**","/js/**","/index").permitAll()   //任意放行的请求路径
                .antMatchers("/user/**").hasRole("ADMIN")       //需要ADMIN角色才能访问的路径
                .and()
                .formLogin()    //使用基于Form表单的登陆验证
                .loginPage("/login").failureUrl("login-error"); //定义登陆页面与失败页面
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)throws Exception{
        auth.inMemoryAuthentication()   //将认证信息储存在内存中
            .withUser("user1").password("123456").roles("ADMIN");   //新增用户user1并为其分配角色
    }

}
