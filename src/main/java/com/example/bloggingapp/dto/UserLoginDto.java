package com.example.bloggingapp.dto;

import java.util.Set;

public record UserLoginDto(
        String username,
        Set<UserFollowDto> following,
        String profilePicture,
        String token
) {
}