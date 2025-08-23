package com.example.bloggingapp.service.impl;


import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.UserRepository;
import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void follow(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.equals(user)) throw new IllegalArgumentException("You cannot follow yourself!");
        if (user.getFollowers().contains(authUser))
            throw new IllegalArgumentException("You are already following this user!");
        userRepository.follow(user.getId(), authUser.getId());
    }

    @Override
    public void unfollow(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.getId().equals(user.getId()))
            throw new IllegalArgumentException("You cannot unfollow yourself!");
        if (!authUser.getFollowing().contains(user))
            throw new IllegalArgumentException("You aren't following this user!");
        userRepository.unfollow(user.getId(), authUser.getId());
    }

    @Override
    public void block(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.getId().equals(user.getId())) throw new IllegalArgumentException("You cannot block yourself!");
        if (authUser.getBlockedUsers().contains(user))
            throw new IllegalArgumentException("You have already blocked this user!");
        userRepository.block(user.getId(), authUser.getId());
    }

    @Override
    public void unblock(User user, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUser.getId().equals(user.getId())) throw new IllegalArgumentException("You cannot unblock yourself!");
        if (!authUser.getBlockedUsers().contains(user))
            throw new IllegalArgumentException("You haven't blocked this user!");
        userRepository.unblock(user.getId(), authUser.getId());
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