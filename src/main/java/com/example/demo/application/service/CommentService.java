package com.example.demo.application.service;

import com.example.demo.application.port.in.ArticleQueryUseCase;
import com.example.demo.application.port.in.CommentUseCase;
import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.application.port.out.CommentRepository;
import com.example.demo.application.port.out.UserRepository;
import com.example.demo.domain.model.Comment;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.shared.exception.NotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
public class CommentService implements CommentUseCase {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final Clock clock;

    public CommentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            ArticleRepository articleRepository,
            Clock clock) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.clock = clock;
    }

    @Override
    public ArticleQueryUseCase.CommentView addComment(String userId, AddCommentCommand command) {
        articleRepository.findPublishedById(command.articleId())
                .orElseThrow(() -> new NotFoundException("Article not found: " + command.articleId()));
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                command.articleId(),
                user.id(),
                user.fullName(),
                command.content().trim(),
                Instant.now(clock));
        Comment saved = commentRepository.save(comment);
        return new ArticleQueryUseCase.CommentView(
                saved.id(),
                saved.userId(),
                saved.userDisplayName(),
                saved.content(),
                saved.createdAt());
    }
}
