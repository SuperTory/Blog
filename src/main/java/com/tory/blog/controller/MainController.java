package com.tory.blog.controller;

import com.tory.blog.entity.Authority;
import com.tory.blog.entity.User;
import com.tory.blog.service.AuthorityService;
import com.tory.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private static final Long ROLE_USER_AUTHORITY_ID = 2L;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;


    @GetMapping("index")
    public String index(){
        return "redirect:/blogs";
    }

    @GetMapping("/")
    public String root(){
        return "redirect:/index";
    }

    @GetMapping("/login")
    public String login(){
        return "user/login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError",true);
        model.addAttribute("errMsg","登陆失败！");
        return "user/login";
    }

    @GetMapping("/register")
    public String register(User user) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
        user.setAuthorities(authorities);
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/test/csrf")
    public String testCsrf(){
        return "test/csrf_test";
    }
}
