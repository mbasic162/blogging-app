package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.config.FileStorageConfig;
import com.example.bloggingapp.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public String save(MultipartFile image) {
        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        UUID uuid = UUID.randomUUID();
        Path path = Paths.get(FileStorageConfig.PROFILE_PICTURE_DIR, uuid + "." + extension);
        byte[] bytes;
        try {
            bytes = image.getBytes();
        } catch (IOException ex) {
            throw new RuntimeException("Invalid image!");
        }
        for (int i = 0; Files.exists(path); i++) {
            uuid = UUID.randomUUID();
            path = Paths.get(FileStorageConfig.PROFILE_PICTURE_DIR, uuid + "." + extension);
            if (i == 1000) {
                throw new RuntimeException("Error processing image!");
            }
        }
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error processing image!");
        }
        return uuid + "." + extension;
    }

    @Override
    public boolean isValid(MultipartFile image) {
        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        return extension != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png"));
    }
}
