package com.example.bloggingapp.service;

import com.example.bloggingapp.model.User;

import java.util.Optional;

public interface UserService {
    void follow(User user, String parentUsername);

    void unfollow(User user, String parentUsername);

    void block(User user, String parentUsername);

    void unblock(User user, String parentUsername);

    void checkAllowViewing(User user);

    void checkAllowViewingAuth(User user, String authUsername);

    boolean isBlockedByOrPrivate(User user, String authUsername);

    Optional<User> findByUsername(String username);
}
