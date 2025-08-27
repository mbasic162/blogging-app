package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.User;

import java.util.Optional;
import java.util.Set;

public interface CommentService {
    Comment save(Comment comment);

    Comment create(CreateCommentRequest request, String username);

    Set<Comment> findByUsername(String username);

    Set<Comment> findByParentPostId(Long postId);

    Set<Comment> findByParentPostIdAuth(Long postId, String username);

    Set<Comment> findByParentCommentId(Long commentId);

    Set<Comment> findByParentCommentIdAuth(Long commentId, String username);

    void filterCommentsAuth(Set<Comment> comments, User authUser);

    void filterComments(Set<Comment> comments);

    void checkAllowViewing(Comment comment);

    void checkAllowViewingAuth(Comment comment, String authUsername);

    Optional<Comment> findById(Long commentId);

    String getUriByIdAndContent(Long commentId, String content);

    Long getIdByUri(String url);

    void like(String username, Long commentId);

    void removeLike(String username, Long commentId);

    void dislike(String username, Long commentId);

    void removeDislike(String username, Long commentId);

    void tempDelete(String username, Long commentId);

    void undelete(String username, Long commentId);

    void permanentlyDelete(String username, Long commentId);

    void hide(String username, Long commentId);

    void unhide(String username, Long commentId);
}
