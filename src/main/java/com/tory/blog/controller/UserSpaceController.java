package com.tory.blog.controller;

import com.tory.blog.entity.Blog;
import com.tory.blog.entity.Catalog;
import com.tory.blog.entity.User;
import com.tory.blog.entity.Vote;
import com.tory.blog.service.BlogService;
import com.tory.blog.service.CatalogService;
import com.tory.blog.service.UserService;
import com.tory.blog.util.ConstraintViolationExceptionHandler;
import com.tory.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;


@Controller
@RequestMapping("/u")
public class UserSpaceController {
    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username) {
        return "redirect:/u/" + username + "/blogs";
    }

    //获取博客列表
    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(@PathVariable("username") String username,
                                   @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                   @RequestParam(value = "catalog", required = false) Long catalogId,
                                   @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                   @RequestParam(value = "async", required = false) boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                   Model model) {
        boolean isCatalogsOwner = false;

        // 判断操作用户是否是博客的所有者
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (currentUser !=null && username.equals(currentUser.getUsername())) {
                isCatalogsOwner = true;
            }
        }

        Page<Blog> page = null;
        User user=(User)userDetailsService.loadUserByUsername(username);

        if (catalogId != null && catalogId > 0) { // 分类查询
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Sort sort = Sort.by(Sort.Order.desc("readSize"));
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogByCatalog(catalog, pageable);
            order = "";
        } else if (order.equals("hot")) { // 最热查询
            Sort sort = Sort.by(Sort.Order.desc("readSize"));
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByTitleLikeAndSort(user, keyword, pageable);
        } else if (order.equals("new")) { // 最新查询
            Sort sort = Sort.by(Sort.Order.desc("readSize"));
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByTitleLike(user, keyword, pageable);
        }


        List<Blog> list = page.getContent();    // 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("catalogs", catalogService.listCatalogs(user));
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        model.addAttribute("isCatalogsOwner", isCatalogsOwner);
        return (async == true ? "/userspace/u :: #mainContainerRepleace" : "/userspace/u");
    }

    //阅读博客
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username,@PathVariable("id") Long id, Model model) {
        // 每次访问，阅读量增加1次
        blogService.readingIncrease(id);

        boolean isBlogOwner = false;
        User currentUser=null;

        // 判断操作用户是否是博客的所有者
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (currentUser !=null && username.equals(currentUser.getUsername())) {
                isBlogOwner = true;
            }
        }

        // 判断当前用户是否已经点过赞
        List<Vote> votes = blogService.getBlogById(id).getVotes();
        Vote currentVote = null; // 当前用户的点赞

        if (currentUser !=null) {
            for (Vote vote : votes) {
                if(vote.getUser().getUsername().equals(currentUser.getUsername())) {
                    currentVote = vote;
                    break;
                }
            }
        }

        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blogModel",blogService.getBlogById(id));
        model.addAttribute("currentVote",currentVote);

        return "/userspace/blog";
    }

    //新增博客页面
    @GetMapping("/{username}/blogs/edit")
    public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
        User user=(User)userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);
        model.addAttribute("catalogs",catalogs);
        model.addAttribute("blog", new Blog(null, null, null));
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    //获取编辑博客的界面
    @GetMapping("/{username}/blogs/edit/{id}")
    public ModelAndView editBlog(@PathVariable("username") String username,@PathVariable("id") Long id, Model model) {
        User user=(User)userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);
        model.addAttribute("catalogs",catalogs);
        model.addAttribute("blog", blogService.getBlogById(id));
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    //保存博客
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
        // 对 Catalog 进行判空处理
        if (blog.getCatalog().getId() == null) {
            return ResponseEntity.ok().body(new Response(false,"未选择分类"));
        }
        try {
            if (blog.getId()!=null) {   //修改博客
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                orignalBlog.setCatalog(blog.getCatalog());
//                orignalBlog.setTags(blog.getTags());
                blogService.saveBlog(orignalBlog);
            } else {                    //新增博客
                User user = (User)userDetailsService.loadUserByUsername(username);
                blog.setUser(user);
                blogService.saveBlog(blog);
            }
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/blog/u/" + username + "/blogs/" + blog.getId();
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    //删除博客
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username,@PathVariable("id") Long id) {
        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/blog/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }


    //返回用户信息页面
    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("/userspace/profile", "userModel", model);
    }

    //保存用户信息
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username, User user) {
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
        User user = (User) userDetailsService.loadUserByUsername(username);
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
