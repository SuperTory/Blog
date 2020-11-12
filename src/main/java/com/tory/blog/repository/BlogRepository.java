package com.tory.blog.repository;

import com.tory.blog.entity.Catalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tory.blog.entity.Blog;
import com.tory.blog.entity.User;


public interface BlogRepository extends JpaRepository<Blog, Long>{
	/**
	 * 根据用户名分页查询博客列表
	 * @param user
	 * @param title
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);
	
	/**
	 * 根据用户名分页查询博客列表
	 * @param user
	 * @param title
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);

	/**
	 * 根据分类查询博客
	 * @param catalog 博客分类
	 * @param pageable 分页对象
	 * @return 博客查询结果
	 */
	Page<Blog> findByCatalog(Catalog catalog, Pageable pageable);
}
