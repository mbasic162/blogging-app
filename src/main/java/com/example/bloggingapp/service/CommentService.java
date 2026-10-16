package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentService {
    void save(Comment comment);

    void create(CreateCommentRequest request, User authUser);

    List<Comment> search(Pageable pageable, String query, User authUser);

    Set<Comment> findByUser(User user, User authUser);

    Set<Comment> findByParentPost(String postURI, User authUser);

    Set<Comment> findByParentComment(String commentURI, User authUser);

    Comment getCommentForViewByURI(String commentURI, User authUser);

    boolean existsByURI(String commentURI);

    Optional<Comment> findById(Long commentId);

    Optional<Comment> findByURI(String commentURI);

    String getURIByIdAndContent(Long commentId, String content);

    Long getIdByURI(String url);

    Boolean isLikedByUser(Comment comment, User user);

    Boolean isDislikedByUser(Comment comment, User user);

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
