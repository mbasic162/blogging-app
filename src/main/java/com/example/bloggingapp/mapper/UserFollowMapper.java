package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.UserFollowDto;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserFollowMapper {
    UserFollowMapper INSTANCE = Mappers.getMapper(UserFollowMapper.class);

    UserFollowDto toDto(User user);

    User toEntity(UserFollowDto userFollowDto);
}