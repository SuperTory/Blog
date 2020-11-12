package com.tory.blog.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tory.blog.entity.Catalog;
import com.tory.blog.entity.User;
import com.tory.blog.service.CatalogService;
import com.tory.blog.util.ConstraintViolationExceptionHandler;
import com.tory.blog.vo.CatalogVO;
import com.tory.blog.vo.Response;
 

/**
 * 分类 控制器.
 */
@Controller
@RequestMapping("/catalogs")
public class CatalogController {
	
	@Autowired
	private CatalogService catalogService;

	@Qualifier("userServiceImpl")
	@Autowired
	private UserDetailsService userDetailsService;

	// 获取分类列表
	@GetMapping
	public String listComments(@RequestParam(value="username",required=true) String username, Model model) {
		User user = (User)userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);

		// 判断操作用户是否是分类的所有者
		boolean isOwner = false;
		
		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null && user.getUsername().equals(principal.getUsername())) {
				isOwner = true;
			} 
		} 
		
		model.addAttribute("isCatalogsOwner", isOwner);
		model.addAttribute("catalogs", catalogs);
		return "/userspace/u :: #catalogRepleace";
	}

	// 保存分类
	@PostMapping
	@PreAuthorize("authentication.name.equals(#catalogVO.username)")// 指定用户才能操作方法
	public ResponseEntity<Response> create(@RequestBody CatalogVO catalogVO) {
		
		String username = catalogVO.getUsername();
		Catalog catalog = catalogVO.getCatalog();
		
		User user = (User)userDetailsService.loadUserByUsername(username);
		
		try {
			catalog.setUser(user);
			catalogService.saveCatalog(catalog);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "分类保存成功", null));
	}
	
	//删除分类
	@DeleteMapping("/{id}")
	@PreAuthorize("authentication.name.equals(#username)")  // 指定用户才能操作方法
	public ResponseEntity<Response> delete(String username, @PathVariable("id") Long id) {
		try {
			catalogService.removeCatalog(id);
		} catch (ConstraintViolationException e)  {
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		
		return ResponseEntity.ok().body(new Response(true, "删除分类成功", null));
	}
	
	//新增分类界面
	@GetMapping("/edit")
	public String getCatalogEdit(Model model) {
		Catalog catalog = new Catalog(null, null);
		model.addAttribute("catalog",catalog);
		return "/userspace/catalogedit";
	}

	//编辑某个分类的页面
	@GetMapping("/edit/{id}")
	public String getCatalogById(@PathVariable("id") Long id, Model model) {
		Catalog catalog = catalogService.getCatalogById(id);
		model.addAttribute("catalog",catalog);
		return "/userspace/catalogedit";
	}
	
}