package com.tory.blog.repository;

import com.tory.blog.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {
    //根据方法名生成查询函数
    List<User> findByUsername(String username);

    //使用Distinct
    List<User> findDistinctByUsername(String username);

    //使用OrderBy
    List<User> findByUsernameOrderByCreateTime(String username);
}
