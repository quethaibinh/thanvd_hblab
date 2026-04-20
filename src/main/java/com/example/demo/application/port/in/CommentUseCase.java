package com.example.demo.application.port.in;

public interface CommentUseCase {
    ArticleQueryUseCase.CommentView addComment(String userId, AddCommentCommand command);

    record AddCommentCommand(String articleId, String content) {
    }
}
