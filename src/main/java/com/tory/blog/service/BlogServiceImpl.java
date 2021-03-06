package com.tory.blog.service;

import javax.transaction.Transactional;

import com.tory.blog.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tory.blog.repository.BlogRepository;


@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private EsBlogService esBlogService;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        boolean isNew = (blog.getId() == null);
        EsBlog esBlog = null;

        Blog returnBlog = blogRepository.save(blog);

        if (isNew) {
            esBlog = new EsBlog(returnBlog);
        } else {
            esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
            esBlog.update(returnBlog);
        }

        esBlogService.updateEsBlog(esBlog);
        return returnBlog;
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
        EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
        esBlogService.removeEsBlog(esblog.getId());
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

    /**
     * 点赞
     *
     * @param blogId 博客ID
     * @return 更改后的博客
     */
    @Override
    public Blog createVote(Long blogId) {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote=new Vote(user);
        Blog originalBlog=this.getBlogById(blogId);
        originalBlog.addVote(vote);
        return this.saveBlog(originalBlog);
    }

    /**
     * 取消点赞
     *
     * @param blogId 博客ID
     * @param voteId 点赞ID
     */
    @Override
    public void removeVote(Long blogId, Long voteId) {
        Blog originalBlog=this.getBlogById(blogId);
        originalBlog.removeVote(voteId);
        this.saveBlog(originalBlog);
    }

    /**
     * 根据分类返回博客
     * @param catalog 博客分类
     * @param pageable 分页
     * @return 博客列表
     */
    @Override
    public Page<Blog> listBlogByCatalog(Catalog catalog, Pageable pageable) {
        return blogRepository.findByCatalog(catalog,pageable);
    }
}
