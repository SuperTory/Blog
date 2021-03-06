package com.tory.blog.service;

import com.tory.blog.entity.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tory.blog.entity.Blog;
import com.tory.blog.entity.User;


public interface BlogService {
	/**
	 * 保存Blog
	 * @param Blog
	 * @return
	 */
	Blog saveBlog(Blog blog);
	
	/**
	 * 删除Blog
	 * @param id
	 * @return
	 */
	void removeBlog(Long id);
	
	/**
	 * 更新Blog
	 * @param Blog
	 * @return
	 */
	Blog updateBlog(Blog blog);
	
	/**
	 * 根据id获取Blog
	 * @param id
	 * @return
	 */
	Blog getBlogById(Long id);
	
	/**
	 * 根据用户名进行分页模糊查询（最新）
	 * @param user
	 * @return
	 */
	Page<Blog> listBlogsByTitleLike(User user, String title, Pageable pageable);
 
	/**
	 * 根据用户名进行分页模糊查询（最热）
	 * @param user
	 * @return
	 */
	Page<Blog> listBlogsByTitleLikeAndSort(User user, String title, Pageable pageable);
	
	/**
	 * 阅读量递增
	 * @param id
	 */
	void readingIncrease(Long id);

	/**
	 * 新增评论
	 * @param blogId 博客ID
	 * @param commentString 评论内容
	 * @return 更改后的博客
	 */
	Blog createComment(Long blogId, String commentString);

	/**
	 * 移除评论
	 * @param blogId 博客ID
	 * @param commentId 评论ID
	 */
	void removeComment(Long blogId, Long commentId);

	/**
	 * 点赞
	 * @param blogId 博客ID
	 * @return 更改后的博客
	 */
	Blog createVote(Long blogId);

	/**
	 * 取消点赞
	 * @param blogId 博客ID
	 * @param voteId 点赞ID
	 */
	void removeVote(Long blogId, Long voteId);

	/**
	 * 根据分类返回博客
	 * @param catalog 博客分类
	 * @param pageable 分页
	 * @return 博客列表
	 */
	Page<Blog> listBlogByCatalog(Catalog catalog, Pageable pageable);
}
