package com.example.bloggingapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileStorageConfig {

    public static String PROFILE_PICTURE_DIR;

    @Value("${storage.image-dir}")
    public void setProfilePictureDir(String dir) {
        PROFILE_PICTURE_DIR = dir;
    }
}
