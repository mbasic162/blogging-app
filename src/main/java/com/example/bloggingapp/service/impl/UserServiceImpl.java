package com.example.bloggingapp.service.impl;


import com.example.bloggingapp.config.FileStorageConfig;
import com.example.bloggingapp.dto.request.EmailChangeRequest;
import com.example.bloggingapp.dto.request.PasswordChangeRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.UserRepository;
import com.example.bloggingapp.security.JwtUtils;
import com.example.bloggingapp.service.ImageService;
import com.example.bloggingapp.service.UserService;
import com.example.bloggingapp.service.helper.ViewHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ImageService imageService;
    private final ViewHelper viewHelper;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Boolean existsByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Boolean isUsernameBlocked(User parent, String username) {
        return userRepository.isUsernameBlocked(parent, username);
    }

    @Override
    public Boolean isUserBlocked(User parent, User user) {
        return userRepository.isUserBlocked(parent, user);
    }

    @Override
    public Boolean isFollowingUser(User parent, User user) {
        return userRepository.isFollowingUser(parent, user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> search(Pageable pageable, String query, User authUser) {
        List<User> users = userRepository.search(pageable, query);
        if (authUser == null) {
            viewHelper.filterUsers(users);
            return users;
        }
        viewHelper.filterUsers(users, authUser);
        return users;
    }

    @Override
    public User getUserForViewByUsername(String username, User authUser) {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!viewHelper.isUserViewable(user)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser == null) {
            viewHelper.filterUserContent(user);
            return user;
        }
        if (isUserBlocked(authUser, user)) {
            return new User(username);
        }
        if (isUserBlocked(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        viewHelper.filterUserContent(user, authUser);
        return user;
    }

    @Override
    public Set<User> findFollowers(String username, User authUser) {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Set<User> followers = user.getFollowers();
        if (authUser == null) {
            if (!viewHelper.isUserViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            viewHelper.filterUsers(followers);
            return followers;
        }
        if (!viewHelper.isUserViewable(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        viewHelper.filterUsers(followers, authUser);
        return followers;
    }

    @Override
    public Set<User> findFollowing(String username, User authUser) {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Set<User> following = user.getFollowing();
        if (authUser == null) {
            if (!viewHelper.isUserViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            viewHelper.filterUsers(following);
            return following;
        }
        if (!viewHelper.isUserViewable(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        viewHelper.filterUsers(following, authUser);
        return following;
    }

    @Override
    public Set<User> findBlocked(User authUser) {
        Set<User> blockedUsers = authUser.getBlockedUsers();
        blockedUsers.removeIf(blockedUser -> !viewHelper.isUserViewable(blockedUser) || isUserBlocked(blockedUser, authUser));
        return blockedUsers;
    }

    @Override
    public void follow(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!viewHelper.isUserViewable(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot follow yourself!");
        }
        if (isFollowingUser(authUser, user)) {
            throw new IllegalStateException("You already follow this user!");
        }
        userRepository.follow(user.getId(), authUser.getId());
    }

    @Override
    public void unfollow(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!viewHelper.isUserViewable(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot unfollow yourself!");
        }
        if (!isFollowingUser(authUser, user)) {
            throw new IllegalStateException("You aren't following this user!");
        }
        userRepository.unfollow(user.getId(), authUser.getId());
    }

    @Override
    public void block(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!viewHelper.isUserViewable(user) || isUserBlocked(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot block yourself!");
        }
        if (isUserBlocked(authUser, user)) {
            throw new IllegalStateException("You already blocked this user!");
        }
        if (isFollowingUser(authUser, user)) {
            userRepository.unfollow(user.getId(), authUser.getId());
        }
        if (isFollowingUser(user, authUser)) {
            userRepository.unfollow(authUser.getId(), user.getId());
        }
        userRepository.block(user.getId(), authUser.getId());
    }

    @Override
    public void unblock(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!viewHelper.isUserViewable(user) || isUserBlocked(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot unblock yourself!");
        }
        if (!isUserBlocked(authUser, user)) {
            throw new IllegalStateException("You haven't blocked this user!");
        }
        userRepository.unblock(user.getId(), authUser.getId());
    }

    @Override
    public String changeUsername(String newUsername, String authUsername) {
        if (authUsername.equalsIgnoreCase(newUsername)) {
            throw new IllegalArgumentException("New username must be different from the old one!");
        }
        if (userRepository.existsByUsernameIgnoreCase(newUsername)) {
            throw new IllegalStateException("Username is already in use!");
        }
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        userRepository.changeUsername(authUser, newUsername);
        return jwtUtils.generateToken(newUsername, authUser.getRoles());
    }

    @Override
    public void changeEmail(EmailChangeRequest request, String authUsername) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(authUsername, request.password()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect password");
        }
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.getEmail().equalsIgnoreCase(request.newEmail())) {
            throw new IllegalArgumentException("New email must be different from the old one!");
        }
        if (userRepository.existsByEmailIgnoreCase(request.newEmail())) {
            throw new IllegalStateException("Email is already in use!");
        }
        userRepository.changeEmail(authUser, request.newEmail());
    }

    @Override
    public void changePassword(PasswordChangeRequest request, String authUsername) {
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(authUsername, request.password()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect password");
        }
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (passwordEncoder.matches(request.newPassword(), authUser.getPassword())) {
            return;
        }
        userRepository.changePassword(authUser, encodedPassword);
    }

    @Override
    public void changeDescription(String newDescription, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        newDescription = newDescription == null ? "" : newDescription;
        if (authUser.getDescription() != null && authUser.getDescription().equals(newDescription)) {
            throw new IllegalArgumentException("New description must be different from the old one!");
        }
        userRepository.changeDescription(authUser, newDescription);
    }

    @Override
    public void changeProfilePicture(MultipartFile newProfilePicture, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (newProfilePicture == null || newProfilePicture.isEmpty()) {
            userRepository.changeProfilePictureName(authUser, "default.jpg");
            return;
        }
        if (!imageService.isValid(newProfilePicture)) {
            throw new IllegalArgumentException("Profile picture is invalid!");
        }
        String profilePictureName = authUser.getProfilePictureName();
        if (authUser.getProfilePictureName() == null) {
            profilePictureName = "default.jpg";
        }
        Path path = Paths.get(FileStorageConfig.PROFILE_PICTURE_DIR, profilePictureName);
        userRepository.changeProfilePictureName(authUser, imageService.save(newProfilePicture));
        if (!profilePictureName.equals("default.jpg")) {
            try {
                Files.delete(path);
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void goPrivate(String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.getPrivate()) {
            throw new IllegalStateException("You are already private!");
        }
        userRepository.goPrivate(authUser);
    }

    @Override
    public void goPublic(String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!authUser.getPrivate()) {
            throw new IllegalStateException("You are already public!");
        }
        userRepository.goPublic(authUser);
    }

    @Override
    public void tempDelete(String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.getDeleted()) {
            throw new IllegalStateException("User is already deleted!");
        }
        userRepository.tempDelete(authUser);
    }

    @Override
    public void undelete(String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!authUser.getDeleted()) {
            throw new IllegalStateException("User is not deleted!");
        }
        userRepository.undelete(authUser);
    }

    @Override
    public void permanentlyDelete(String authUsername, String password) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(authUsername, password));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect password!");
        }
        userRepository.delete(authUser);
    }

    @Override
    public void disable(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!user.getEnabled()) {
            throw new IllegalStateException("User is already disabled!");
        }
        userRepository.disable(user);
    }

    @Override
    public void enable(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (user.getEnabled()) {
            throw new IllegalStateException("User is already enabled!");
        }
        userRepository.enable(user);
    }
}