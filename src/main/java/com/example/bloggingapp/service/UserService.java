package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.EmailChangeRequest;
import com.example.bloggingapp.dto.request.PasswordChangeRequest;
import com.example.bloggingapp.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    void follow(User user, String authUsername);

    void unfollow(User user, String authUsername);

    void block(User user, String authUsername);

    void unblock(User user, String authUsername);

    String changeUsername(String newUsername, String authUsername);

    void changeEmail(EmailChangeRequest request, String authUsername);

    void changePassword(PasswordChangeRequest request, String authUsername);

    void changeDescription(String newDescription, String authUsername);

    void goPrivate(String authUsername);

    void goPublic(String authUsername);

    void tempDelete(String authUsername);

    void undelete(String authUsername);

    void permanentlyDelete(String authUsername, String password);

    void disable(String username);

    void enable(String username);

    void checkAllowViewing(User user);

    void checkAllowViewingAuth(User user, String authUsername);

    boolean isBlockedByOrPrivate(User user, User authUser);

}
