package com.tory.blog.repository;

import com.tory.blog.entity.Catalog;
import com.tory.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    List<Catalog> findByUser(User user);

    List<Catalog> findByUserAndName(User user, String name);
}
