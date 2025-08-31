package com.example.bloggingapp.service;

import com.example.bloggingapp.model.Post;

import java.util.Optional;
import java.util.Set;

public interface PostService {
    Set<Post> findByUsername(String username);

    Set<Post> findByUsernameAuth(String username, String authUsername);

    Optional<Post> findById(Long id);

    Post save(Post post);

    Set<Post> findN(int n);

    Set<Post> findNAuth(int n, String authUsername);

    String getUriByIdAndTitle(Long postId, String title);

    Long getIdByUri(String uri);

    void checkAllowViewing(Post post);

    void checkAllowViewingAuth(Post post, String authUsername);

    void like(String username, Long postId);

    void removeLike(String username, Long postId);

    void dislike(String username, Long postId);

    void removeDislike(String username, Long postId);

    void tempDelete(String username, Long postId);

    void undelete(String username, Long postId);

    void permanentlyDelete(String username, Long postId);

    void hide(String username, Long postId);

    void unhide(String username, Long postId);
}
