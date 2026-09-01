package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.UserFollowDto;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserFollowMapper {
    UserFollowDto toDto(User user);
}