package com.tory.blog.controller;

import com.tory.blog.entity.Article;
import com.tory.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("article")
@RestController
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("search")
    public List<Article> searchBlog(@RequestParam("title") String title){
        Pageable pageable= PageRequest.of(0,20);
        Page<Article> blogPage= articleRepository.findDistinctArticleByTitleContaining(title,pageable);
        return blogPage.getContent();
    }
}
