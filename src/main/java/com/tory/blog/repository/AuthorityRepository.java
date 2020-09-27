package com.tory.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tory.blog.entity.Authority;


public interface AuthorityRepository extends JpaRepository<Authority, Long>{
}
