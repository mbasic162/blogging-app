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

    Set<Comment> findByUser(User user, String authUsername);

    Set<Comment> findByParentPost(String postURI, String authUsername);

    Set<Comment> findByParentComment(String commentURI, String authUsername);

    Comment getCommentForViewByURI(String commentURI, String authUsername);

    void filterCommentsAuth(Set<Comment> comments, User authUser);

    void filterComments(Set<Comment> comments);

    Optional<Comment> findById(Long commentId);

    String getURIByIdAndContent(Long commentId, String content);

    Long getIdByURI(String url);

    Integer getViewableCommentCountByPost(Post post, String authUsername);

    void like(Long commentId, String authUsername);

    void removeLike(Long commentId, String authUsername);

    void dislike(Long commentId, String authUsername);

    void removeDislike(Long commentId, String authUsername);

    void tempDelete(Long commentId, String authUsername);

    void undelete(Long commentId, String authUsername);

    void permanentlyDelete(Long commentId, String authUsername);

    void hide(Long commentId, String authUsername);

    void unhide(Long commentId, String authUsername);

    void changeContent(Long commentId, String authUsername, String newContent);

    void tempDeleteByAdmin(Long commentId);

    void undeleteByAdmin(Long commentId);

    boolean isViewable(Comment comment);

    boolean isViewableAuth(Comment comment, User authUser);
}
