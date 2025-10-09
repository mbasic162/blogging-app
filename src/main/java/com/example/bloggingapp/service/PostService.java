package com.example.bloggingapp.service;

import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;

import java.util.Optional;
import java.util.Set;

public interface PostService {
    Set<Post> findByUser(User user, String authUsername);

    Optional<Post> findById(Long id);

    Post save(Post post);

    Set<Post> findN(int n, String authUsername);

    String getUriByTitleAndId(String title, Long postId);

    Long getIdByUri(String uri);

    boolean isViewable(Post post, String authUsername);

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
