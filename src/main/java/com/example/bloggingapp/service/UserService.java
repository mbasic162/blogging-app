package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.model.User;

import java.util.Optional;

public interface UserService {
    void follow(User user, String authUsername);

    void unfollow(User user, String authUsername);

    void block(User user, String authUsername);

    void unblock(User user, String authUsername);

    void tempDelete(String authUsername);

    void undelete(String authUsername);

    void permanentlyDelete(String authUsername, LoginRequest request);

    void checkAllowViewing(User user);

    void checkAllowViewingAuth(User user, String authUsername);

    boolean isBlockedByOrPrivate(User user, String authUsername);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
