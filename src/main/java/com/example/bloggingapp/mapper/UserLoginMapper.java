package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.UserLoginDto;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class})
public interface UserLoginMapper {
    UserLoginMapper INSTANCE = Mappers.getMapper(UserLoginMapper.class);

    @Mapping(target = "profilePicture", source = "user", qualifiedByName = "mapProfilePicture")
    @Mapping(target = "token", source = "token")
    UserLoginDto toDto(User user, String token);
}
