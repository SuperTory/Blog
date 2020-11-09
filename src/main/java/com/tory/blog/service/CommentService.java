package com.tory.blog.service;

import com.tory.blog.entity.Comment;

public interface CommentService {
    Comment getCommentById(Long id);

    void removeComment(Long id);
}
