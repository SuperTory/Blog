package com.tory.blog.controller;

import com.tory.blog.entity.User;
import com.tory.blog.service.UserService;
import com.tory.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/u")
public class UserSpaceController {
	@Qualifier("userServiceImpl")
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;
 
	@GetMapping("/{username}")
	public String userSpace(@PathVariable("username") String username) {
		return "blog/homepage";
	}
 
	@GetMapping("/{username}/blogs")
	public String listBlogsByOrder(@PathVariable("username") String username,
			@RequestParam(value="order",required=false,defaultValue="new") String order,
			@RequestParam(value="category",required=false ) Long category,
			@RequestParam(value="keyword",required=false ) String keyword) {
		
		if (category != null) {
			return "blog/homepage";
		} else if (keyword != null && !keyword.isEmpty()) {
			return "blog/homepage";
		}  
		
		System.out.print("order:" +order);
		System.out.print("selflink:" + "redirect:/u/"+ username +"/blogs?order="+order);
		return "blog/homepage";
	}
	
	@GetMapping("/{username}/blogs/{id}")
	public String listBlogsByOrder(@PathVariable("id") Long id) {
		return "blog/detail";
	}
	
	
	@GetMapping("/{username}/blogs/edit")
	public String editBlog() {
		return "blog/edit";
	}


	//返回用户信息页面
	@GetMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView profile(@PathVariable("username") String username, Model model) {
		User user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("/userspace/profile", "userModel", model);
	}

	//保存用户信息
	@PostMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)")
	public String saveProfile(@PathVariable("username") String username,User user) {
		User originalUser = userService.getUserById(user.getId());
		originalUser.setEmail(user.getEmail());
		originalUser.setName(user.getName());

		// 判断密码是否做了变更
		String rawPassword = originalUser.getPassword();
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePasswd = encoder.encode(user.getPassword());
		boolean isMatch = encoder.matches(rawPassword, encodePasswd);
		if (!isMatch) {
			originalUser.setEncodePassword(user.getPassword());
		}

		userService.saveUser(originalUser);
		return "redirect:/u/" + username + "/profile";
	}

	//头像编辑页面
	@GetMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView avatar(@PathVariable("username") String username, Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("/userspace/avatar", "userModel", model);
	}

	//保存头像
	@PostMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
		String avatarUrl = user.getAvatar();

		User originalUser = userService.getUserById(user.getId());
		originalUser.setAvatar(avatarUrl);
		userService.saveUser(originalUser);

		return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
	}
}
