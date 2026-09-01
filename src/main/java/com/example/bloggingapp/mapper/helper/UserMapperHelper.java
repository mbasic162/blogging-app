package com.example.bloggingapp.mapper.helper;

import com.example.bloggingapp.model.User;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class UserMapperHelper {

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
