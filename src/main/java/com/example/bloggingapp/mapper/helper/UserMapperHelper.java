package com.example.bloggingapp.mapper.helper;

import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperHelper {
    private final UserService userService;

    @Named("isUserBlocked")
    public Boolean isUserBlocked(User user, @Context User authUser) {
        return userService.isBlockedByUsername(authUser, user.getUsername());
    }

    @Named("mapProfilePicture")
    public String mapProfilePicture(User user) {
        String profilePictureName = user.getProfilePictureName();
        if (profilePictureName == null) {
            return null;
        }
        if (profilePictureName.endsWith(".jpg")) {
            return "data:image/jpg;base64," + user.getProfilePicture();
        }
        if (profilePictureName.endsWith(".png")) {
            return "data:image/png;base64," + user.getProfilePicture();
        }
        return "data:image/jpeg;base64," + user.getProfilePicture();
    }
}