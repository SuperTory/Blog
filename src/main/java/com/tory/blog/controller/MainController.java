package com.tory.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("index")
    public String index(){
        return "index";
    }

    @GetMapping("/")
    public String root(){
        return "redirect:/index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError",true);
        model.addAttribute("errMsg","登陆失败！");
        return "login";
    }
}
