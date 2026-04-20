package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.CommentRepository;
import com.example.demo.domain.model.Comment;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.CommentEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaCommentRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaCommentAdapter implements CommentRepository {

    private final JpaCommentRepository jpaCommentRepository;

    public JpaCommentAdapter(JpaCommentRepository jpaCommentRepository) {
        this.jpaCommentRepository = jpaCommentRepository;
    }

    @Override
    public List<Comment> findByArticleId(String articleId) {
        return jpaCommentRepository.findByArticleIdOrderByCreatedAtDesc(articleId).stream()
                .map(CommentEntity::toDomain)
                .toList();
    }

    @Override
    public Comment save(Comment comment) {
        return jpaCommentRepository.save(CommentEntity.fromDomain(comment)).toDomain();
    }
}
