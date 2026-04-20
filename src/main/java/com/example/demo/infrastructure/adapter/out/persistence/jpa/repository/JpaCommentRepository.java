package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.CommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCommentRepository extends JpaRepository<CommentEntity, String> {
    List<CommentEntity> findByArticleIdOrderByCreatedAtDesc(String articleId);
}
