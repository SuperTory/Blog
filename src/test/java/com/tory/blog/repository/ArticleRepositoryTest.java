package com.tory.blog.repository;

import com.tory.blog.entity.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        articleRepository.save(new Article("1", "静夜思", "李白的诗", "床前明月光，疑是地上霜。"));
        articleRepository.save(new Article("2", "青花瓷", "周杰伦的歌", "素胚勾勒出青花笔锋浓转淡。"));
        articleRepository.save(new Article("3", "青玉案", "辛弃疾的词", "东风夜放花千树，更吹落星如雨。"));
    }

    @Test
    void findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContaining() {
        String title = "青";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Article> blogPage = articleRepository.findDistinctArticleByTitleContaining(title, pageable);

        for (Article blog : blogPage.getContent()) {
            System.out.println(blog);
        }

    }
}