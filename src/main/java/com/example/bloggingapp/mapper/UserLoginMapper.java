package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.UserLoginDto;
import com.example.bloggingapp.mapper.helper.UserMapperHelper;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class})
public interface UserLoginMapper {

    @Mapping(target = "profilePicture", source = "user", qualifiedByName = "mapProfilePicture")
    @Mapping(target = "token", source = "token")
    UserLoginDto toDto(User user, String token);
}
