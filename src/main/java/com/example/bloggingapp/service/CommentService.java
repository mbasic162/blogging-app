package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;

import java.util.Optional;
import java.util.Set;

public interface CommentService {
    Comment save(Comment comment);

    Comment create(CreateCommentRequest request, User authUser);

    Set<Comment> findByUser(User user, User authUser);

    Set<Comment> findByParentPost(String postURI, User authUser);

    Set<Comment> findByParentComment(String commentURI, User authUser);

    Comment getCommentForViewByURI(String commentURI, User authUser);

    boolean existsByURI(String commentURI);

    Optional<Comment> findById(Long commentId);

    String getURIByIdAndContent(Long commentId, String content);

    Long getIdByURI(String url);

    Integer getViewableCommentCountByPost(Post post, User authUser);

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
}
