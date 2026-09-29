package com.example.bloggingapp.dto;

import com.example.bloggingapp.model.User;

import java.util.Set;

public record EditProfileDto(
        String username,
        String email,
        String description,
        Boolean isPrivate,
        Boolean isDeleted,
        String profilePicture,
        Set<User> blockedUsers
) {
}
