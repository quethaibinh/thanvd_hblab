package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.CommentRepository;
import com.example.demo.domain.model.Comment;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

//@Repository
public class InMemoryCommentRepository implements CommentRepository {

    private final Map<String, Comment> commentsById = new ConcurrentHashMap<>();

    @Override
    public Comment save(Comment comment) {
        commentsById.put(comment.id(), comment);
        return comment;
    }

    @Override
    public List<Comment> findByArticleId(String articleId) {
        return commentsById.values().stream()
                .filter(comment -> comment.articleId().equals(articleId))
                .toList();
    }
}
