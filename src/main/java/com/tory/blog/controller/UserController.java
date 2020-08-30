package com.tory.blog.controller;

import com.tory.blog.entity.User;
import com.tory.blog.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserRepo userRepo;

    //查询所有用户信息
    @GetMapping("list")
    public ModelAndView index() {
        return new ModelAndView("user/list", "userList", userRepo.findAll());
    }

    //根据id查询单个用户信息
    @GetMapping("{id}")
    public ModelAndView getById(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        if (userRepo.findById(id).isPresent()) {
            modelAndView.addObject("user", userRepo.findById(id).get());
        }
        modelAndView.setViewName("user/user");
        return modelAndView;
    }

    //新增用户
    @GetMapping("add")
    public ModelAndView add() {
        Object o = new Object();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", new User());
        modelAndView.addObject("title", "添加用户");
        modelAndView.setViewName("user/form");
        return modelAndView;
    }

    //修改用户信息
    @GetMapping("modify/{id}")
    public ModelAndView modify(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", userRepo.findById(id).get());
        modelAndView.addObject("title", "修改信息");
        modelAndView.setViewName("user/form");
        return modelAndView;
    }

    //保存用户user对象
    @PostMapping("save")
    public ModelAndView modify(User user) {
        user.setCreateTime(new Date());
        userRepo.save(user);
        return new ModelAndView("redirect:/blog/user/list");
    }

    //删除用户
    @GetMapping("delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        userRepo.deleteById(id);
        return new ModelAndView("redirect:/blog/user/list");
    }
}