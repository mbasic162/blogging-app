package com.example.bloggingapp.service;

import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostService {
    Set<Post> findByUsername(String username);

    Optional<Post> findById(Long id);

    Post save(Post post);

    List<Post> findN(int n);

    List<Post> findNAuth(int n, String authUsername);

    Optional<String> getTitleById(Long id);

    String getUriByIdAndTitle(Long postId, String title);

    Long getIdByUri(String uri);

    void checkAllowViewing(Post post);

    void checkAllowViewingAuth(Post post, String authUsername);

    void like(String username, Long postId);

    void removeLike(String username, Long postId);

    //Only called by dislike method
    void removeLike(User user, Post post);

    void dislike(String username, Long postId);

    void removeDislike(String username, Long postId);

    //Only called by like method
    void removeDislike(User user, Post post);

    void delete(String username, Long postId);

    void undelete(String username, Long postId);

    void permanentlyDelete(String username, Long postId);

    void hide(String username, Long postId);

    void unhide(String username, Long postId);
}
