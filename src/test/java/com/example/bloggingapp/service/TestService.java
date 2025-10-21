package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.UserDto;

import java.util.Set;

public interface TestService {
    void checkAllowViewingPostDto(PostDto postDto, String authUsername);

    void checkAllowViewingPostDtos(Set<PostDto> postDtos, String authUsername);

    void checkAllowViewingUserDto(UserDto userDto, String authUsername);

    void checkAllowViewingCommentDtos(Set<CommentDto> commentDtos, String authUsername);
}