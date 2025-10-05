package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;

import java.util.Optional;
import java.util.Set;

public interface CommentService {
    Comment save(Comment comment);

    Comment create(CreateCommentRequest request, String authUsername);

    Set<Comment> findByUsername(String username);

    Set<Comment> findByParentPost(Post post, String authUsername);

    Set<Comment> findByParentComment(Comment comment, String authUsername);

    void filterCommentsAuth(Set<Comment> comments, User authUser);

    void filterComments(Set<Comment> comments);

    boolean isViewable(Comment comment, String authUsername);

    Optional<Comment> findById(Long commentId);

    String getUriByIdAndContent(Long commentId, String content);

    Long getIdByUri(String url);

    void like(String authUsername, Long commentId);

    void removeLike(String authUsername, Long commentId);

    void dislike(String authUsername, Long commentId);

    void removeDislike(String authUsername, Long commentId);

    void tempDelete(String authUsername, Long commentId);

    void undelete(String authUsername, Long commentId);

    void permanentlyDelete(String authUsername, Long commentId);

    void hide(String authUsername, Long commentId);

    void unhide(String authUsername, Long commentId);

    void changeContent(String authUsername, Long commentId, String newContent);

    void tempDeleteByAdmin(Long commentId);

    void undeleteByAdmin(Long commentId);
}
