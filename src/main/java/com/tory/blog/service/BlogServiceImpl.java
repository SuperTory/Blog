package com.tory.blog.service;

import javax.transaction.Transactional;

import com.tory.blog.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tory.blog.entity.Blog;
import com.tory.blog.entity.User;
import com.tory.blog.repository.BlogRepository;


@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Blog updateBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.getOne(id);
    }

    @Override
    public Page<Blog> listBlogsByTitleLike(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByTitleLikeAndSort(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }

    @Override
    public void readingIncrease(Long id) {
        Blog blog = blogRepository.getOne(id);
        blog.setReadSize(blog.getReadSize() + 1);
        blogRepository.save(blog);
    }

    /**
     * 新增评论
     *
     * @param blogId  博客ID
     * @param commentString 评论
     * @return 更改后的博客
     */
    @Override
    public Blog createComment(Long blogId, String commentString) {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment=new Comment(user,commentString);
        Blog originalBlog=this.getBlogById(blogId);
        originalBlog.addComment(comment);
        return this.saveBlog(originalBlog);
    }

    /**
     * 移除评论
     *
     * @param blogId    博客ID
     * @param commentId 评论ID
     */
    @Override
    public void removeComment(Long blogId, Long commentId) {
        Blog originalBlog=this.getBlogById(blogId);
        originalBlog.removeComment(commentId);
        this.saveBlog(originalBlog);
    }
}
