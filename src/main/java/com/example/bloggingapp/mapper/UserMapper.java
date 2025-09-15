package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserFollowMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "isAuthUserBlocked", constant = "false")
    @Mapping(target = "isUserBlocked", constant = "false")
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
