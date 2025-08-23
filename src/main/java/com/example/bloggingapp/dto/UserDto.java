package com.example.bloggingapp.dto;

import java.util.Set;

public record UserDto(
        String username,
        Set<UserFollowDto> followers,
        Set<UserFollowDto> following
) {
}
