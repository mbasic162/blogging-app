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
import lombok.RequiredArgsConstructor;
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

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserForViewByUsername(String username, String authUsername) {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUsername.isEmpty()) {
            if (!isViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            return user;
        }
        User authUser = findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.getBlockedUsers().contains(user)) {
            return new User(username, null, null, null, null, false);
        }
        if (user.getBlockedUsers().contains(authUser)) {
            return new User(username, null, null, null, null, false);
        }
        return user;
    }

    @Override
    public Set<User> findFollowers(String username, String authUsername) {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Set<User> followers = user.getFollowers();
        if (authUsername.isEmpty()) {
            if (!isViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            followers.removeIf(follower -> !isViewable(user));
            return followers;
        }
        User authUser = findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!isViewableAuth(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        followers.removeIf(follower -> !isViewableAuth(user, authUser));
        return followers;
    }

    @Override
    public Set<User> findFollowing(String username, String authUsername) {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Set<User> following = user.getFollowing();
        if (authUsername.isEmpty()) {
            if (!isViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            following.removeIf(follower -> !isViewable(user));
            return following;
        }
        User authUser = findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!isViewableAuth(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        following.removeIf(follower -> !isViewableAuth(user, authUser));
        return following;
    }

    @Override
    public void follow(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!isViewableAuth(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot follow yourself!");
        }
        if (user.getFollowers().contains(authUser)) {
            throw new IllegalStateException("You already follow this user!");
        }
        userRepository.follow(user.getId(), authUser.getId());
    }

    @Override
    public void unfollow(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!isViewableAuth(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot unfollow yourself!");
        }
        if (!authUser.getFollowing().contains(user)) {
            throw new IllegalStateException("You aren't following this user!");
        }
        userRepository.unfollow(user.getId(), authUser.getId());
    }

    @Override
    public void block(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!isViewable(user) || user.getBlockedUsers().contains(authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot block yourself!");
        }
        if (authUser.getBlockedUsers().contains(user)) {
            throw new IllegalStateException("You already blocked this user!");
        }
        if (user.getFollowers().contains(authUser)) {
            userRepository.unfollow(user.getId(), authUser.getId());
        }
        if (user.getFollowing().contains(authUser)) {
            userRepository.unfollow(authUser.getId(), user.getId());
        }
        userRepository.block(user.getId(), authUser.getId());
    }

    @Override
    public void unblock(String username, String authUsername) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (!isViewable(user) || user.getBlockedUsers().contains(authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot unblock yourself!");
        }
        if (!authUser.getBlockedUsers().contains(user)) {
            throw new IllegalStateException("You haven't blocked this user!");
        }
        userRepository.unblock(user.getId(), authUser.getId());
    }

    @Override
    public String changeUsername(String newUsername, String authUsername) {
        if (authUsername.equals(newUsername)) {
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
            throw new IllegalArgumentException("New password must be different from the old one!");
        }
        userRepository.changePassword(authUser, encodedPassword);
    }

    @Override
    public void changeDescription(String newDescription, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.getDescription().equals(newDescription)) {
            throw new IllegalArgumentException("New description must be different from the old one!");
        }
        userRepository.changeDescription(authUser, newDescription);
    }

    @Override
    public void changeProfilePicture(MultipartFile profilePicture, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (profilePicture == null || profilePicture.isEmpty()) {
            userRepository.changeProfilePictureName(authUser, "default.jpg");
            return;
        }
        if (!imageService.isValid(profilePicture)) {
            throw new IllegalArgumentException("Profile picture is invalid!");
        }
        String profilePictureName = authUser.getProfilePictureName();
        if (authUser.getProfilePictureName() == null) {
            profilePictureName = "default.jpg";
        }
        Path path = Paths.get(FileStorageConfig.PROFILE_PICTURE_DIR, profilePictureName);
        userRepository.changeProfilePictureName(authUser, imageService.save(profilePicture));
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

    @Override
    public boolean isViewable(User user) {
        return !user.getPrivate() && !user.getDeleted() && user.getEnabled();
    }

    @Override
    public boolean isViewableAuth(User user, User authUser) {
        if (authUser.equals(user)) {
            return true;
        }
        return !user.getPrivate() && !user.getDeleted() && user.getEnabled() && !user.getBlockedUsers().contains(authUser) && !authUser.getBlockedUsers().contains(user);
    }
}