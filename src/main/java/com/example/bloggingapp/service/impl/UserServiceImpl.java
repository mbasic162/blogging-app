package com.example.bloggingapp.service.impl;


import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.UserRepository;
import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;

    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void follow(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.equals(user)) throw new IllegalArgumentException("You cannot follow yourself!");
        if (user.getFollowers().contains(authUser))
            throw new IllegalStateException("You already follow this user!");
        userRepository.follow(user.getId(), authUser.getId());
    }

    @Override
    public void unfollow(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.equals(user))
            throw new IllegalArgumentException("You cannot unfollow yourself!");
        if (!authUser.getFollowing().contains(user))
            throw new IllegalStateException("You aren't following this user!");
        userRepository.unfollow(user.getId(), authUser.getId());
    }

    @Override
    public void block(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.equals(user)) throw new IllegalArgumentException("You cannot block yourself!");
        if (authUser.getBlockedUsers().contains(user))
            throw new IllegalStateException("You already blocked this user!");
        if (user.getFollowers().contains(authUser)) userRepository.unfollow(user.getId(), authUser.getId());
        if (user.getFollowing().contains(authUser)) userRepository.unfollow(authUser.getId(), user.getId());
        userRepository.block(user.getId(), authUser.getId());
    }

    @Override
    public void unblock(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.equals(user)) throw new IllegalArgumentException("You cannot unblock yourself!");
        if (!authUser.getBlockedUsers().contains(user))
            throw new IllegalStateException("You haven't blocked this user!");
        userRepository.unblock(user.getId(), authUser.getId());
    }

    @Override
    public void tempDelete(String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.getDeleted()) {
            throw new IllegalStateException("User is already deleted!");
        }
        userRepository.tempDelete(authUser.getId());
    }

    @Override
    public void undelete(String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!authUser.getDeleted()) {
            throw new IllegalStateException("User is not deleted!");
        }
        userRepository.undelete(authUser.getId());
    }

    @Override
    public void permanentlyDelete(String authUsername, LoginRequest request) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        authManager.authenticate(new UsernamePasswordAuthenticationToken(authUsername, request.password()));
        userRepository.delete(authUser);
    }

    @Override
    public void checkAllowViewing(User user) {
        if (user.getPrivate()) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public void checkAllowViewingAuth(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (((user.getPrivate() && !user.equals(authUser)) || authUser.getBlockedUsers().contains(user) || user.getBlockedUsers().contains(authUser))) {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public boolean isBlockedByOrPrivate(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return user.getPrivate() || user.getBlockedUsers().contains(authUser);
    }
}