package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.EmailChangeRequest;
import com.example.bloggingapp.dto.request.PasswordChangeRequest;
import com.example.bloggingapp.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Optional<User> findByUsername(String username);

    List<User> search(Pageable pageable, String query, User authUser);

    User getUserForViewByUsername(String username, User authUser);

    User save(User user);

    Boolean existsByUsernameIgnoreCase(String username);

    Boolean existsByEmailIgnoreCase(String email);

    Boolean isUsernameBlocked(User parent, String username);

    Boolean isUserBlocked(User parent, User user);

    Boolean isFollowingUser(User parent, User user);

    Set<User> findFollowers(String username, User authUser);

    Set<User> findFollowing(String username, User authUser);

    Set<User> findBlocked(User authUser);

    void follow(String username, String authUsername);

    void unfollow(String username, String authUsername);

    void block(String username, String authUsername);

    void unblock(String username, String authUsername);

    String changeUsername(String newUsername, String authUsername);

    void changeEmail(EmailChangeRequest request, String authUsername);

    void changePassword(PasswordChangeRequest request, String authUsername);

    void changeDescription(String newDescription, String authUsername);

    void changeProfilePicture(MultipartFile newProfilePicture, String authUsername);

    void goPrivate(String authUsername);

    void goPublic(String authUsername);

    void tempDelete(String authUsername);

    void undelete(String authUsername);

    void permanentlyDelete(String authUsername, String password);

    void disable(String username);

    void enable(String username);
}
