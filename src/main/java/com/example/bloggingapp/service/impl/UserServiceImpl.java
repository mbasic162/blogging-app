package com.example.bloggingapp.service.impl;


import com.example.bloggingapp.dto.request.EmailChangeRequest;
import com.example.bloggingapp.dto.request.PasswordChangeRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.UserRepository;
import com.example.bloggingapp.security.JwtUtils;
import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

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
    public void follow(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot follow yourself!");
        }
        if (user.getFollowers().contains(authUser)) {
            throw new IllegalStateException("You already follow this user!");
        }
        userRepository.follow(user.getId(), authUser.getId());
    }

    @Override
    public void unfollow(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.equals(user)) {
            throw new IllegalArgumentException("You cannot unfollow yourself!");
        }
        if (!authUser.getFollowing().contains(user)) {
            throw new IllegalStateException("You aren't following this user!");
        }
        userRepository.unfollow(user.getId(), authUser.getId());
    }

    @Override
    public void block(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (isBlockedByOrPrivate(user, authUser) || user.getDeleted() || !user.getEnabled()) {
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
    public void unblock(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (isBlockedByOrPrivate(user, authUser) || user.getDeleted() || !user.getEnabled()) {
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
        if (authUser.getEmail().equals(request.newEmail())) {
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
    public void checkAllowViewing(User user) {
        if (user.getPrivate() || user.getDeleted() || !user.getEnabled()) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public void checkAllowViewingAuth(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (((user.getPrivate() && !user.equals(authUser)) || user.getDeleted() || authUser.getBlockedUsers().contains(user) || user.getBlockedUsers().contains(authUser))) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public boolean isBlockedByOrPrivate(User user, User authUser) {
        return user.getPrivate() || user.getBlockedUsers().contains(authUser);
    }
}