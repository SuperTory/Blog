package com.tory.blog.repository;

import com.tory.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    //根据方法名生成查询函数
    User findByUsername(String username);

    //使用Distinct
    List<User> findDistinctByUsername(String username);

    /**
     * 根据用户名分页查询用户列表
     *
     * @param name     名字
     * @param pageable 分页
     * @return 分页用户信息
     */
    Page<User> findByNameLike(String name, Pageable pageable);

}
