package com.example.bloggingapp.service;

import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostService {
    Set<Post> findByUsername(String username, User authUser);

    Optional<Post> findById(Long id);

    Post save(Post post);

    List<Post> findN(Integer numberOfPosts, User authUser);

    Set<Post> search(String query, User authUser);

    String getURIByIdAndTitle(Long postId, String title);

    Long getIdByURI(String postURI);

    Post getPostForViewByURI(String postURI, User authUser);

    Boolean isLikedByUser(Post post, User user);

    Boolean isDislikedByUser(Post post, User user);

    boolean existsByURI(String postURI);

    void like(String authUsername, Long postId);

    void removeLike(String authUsername, Long postId);

    void dislike(String authUsername, Long postId);

    void removeDislike(String authUsername, Long postId);

    void changeTitle(String authUsername, Long postId, String newTitle);

    void changeContent(String authUsername, Long postId, String newContent);

    void tempDelete(String authUsername, Long postId);

    void undelete(String authUsername, Long postId);

    void permanentlyDelete(String authUsername, Long postId);

    void hide(String authUsername, Long postId);

    void unhide(String authUsername, Long postId);

    void tempDeleteByAdmin(Long postId);

    void undeleteByAdmin(Long postId);
}
