package com.tory.blog.controller;

import com.tory.blog.entity.User;
import com.tory.blog.service.BlogService;
import com.tory.blog.service.VoteService;
import com.tory.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("votes")
public class VoteController {
    @Autowired
    private VoteService voteService;
    @Autowired
    private BlogService blogService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Response> createVote(Long blogId) {
        blogService.createVote(blogId);
        return ResponseEntity.ok().body(new Response(true, "点赞成功", null));
    }

    @DeleteMapping("/{voteId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Response> removeVote(@PathVariable("voteId") Long voteId, Long blogId) {
        boolean isOwner = false;
        User user = voteService.getVoteById(voteId).getUser();

        // 判断操作用户是否是点赞的所有者
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }
        if (!isOwner) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限"));
        }

        blogService.removeVote(blogId, voteId);
        voteService.removeVote(voteId);

        return ResponseEntity.ok().body(new Response(true, "取消点赞成功", null));
    }

}
