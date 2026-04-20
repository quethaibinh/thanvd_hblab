package com.example.demo.application.port.out;

import com.example.demo.domain.model.Comment;
import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findByArticleId(String articleId);
}
