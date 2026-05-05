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
    @Mapping(target = "profilePicture",
            expression = "java(addPrefix(user))")
    UserDto toDto(User user);

    default String addPrefix(User user) {
        String profilePictureName = user.getProfilePictureName();
        if (profilePictureName == null || profilePictureName.endsWith(".jpg")) {
            return "data:image/jpg;base64," + user.getProfilePicture();
        }
        if (profilePictureName.endsWith(".png")) {
            return "data:image/png;base64," + user.getProfilePicture();
        }
        return "data:image/jpeg;base64," + user.getProfilePicture();

    }
}