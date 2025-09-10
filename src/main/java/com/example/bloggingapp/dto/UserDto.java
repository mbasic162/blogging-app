package com.example.bloggingapp.dto;

import java.util.Set;

public record UserDto(
        String username,
        String description,
        Set<UserFollowDto> followers,
        Set<UserFollowDto> following,
        boolean isBlocked
) {
}