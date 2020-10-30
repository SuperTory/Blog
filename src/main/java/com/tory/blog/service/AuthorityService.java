package com.tory.blog.service;

import com.tory.blog.entity.Authority;

/**
 * Authority 服务接口.
 */
public interface AuthorityService {
    /**
     * 根据id获取 Authority
     *
     * @param id 权限id
     * @return 具体权限Authority
     */
    Authority getAuthorityById(Long id);
}
