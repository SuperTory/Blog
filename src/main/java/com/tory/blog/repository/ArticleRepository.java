package com.tory.blog.repository;

import com.tory.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {
    /**
     * 根据Title、Summary、Content分页查询BLog
     *
     * @param title
     * @return Page<Article>
     */
    Page<Article> findDistinctArticleByTitleContaining(String title, Pageable pageable);
}
