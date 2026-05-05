package com.example.bloggingapp.service.impl;


import com.example.bloggingapp.service.ImageTestService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageTestServiceImpl implements ImageTestService {
    @Override
    public MultipartFile getImage(Path path) {
        String extension = StringUtils.getFilenameExtension(path.getFileName().toString());
        if(extension == null){
            throw new IllegalArgumentException("Invalid image!");
        }
        try {
            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                return new MockMultipartFile("profilePicture", path.getFileName().toString(), "image/jpeg", Files.readAllBytes(path));
            }
            if (extension.equalsIgnoreCase("png")) {
                return new MockMultipartFile("profilePicture", path.getFileName().toString(), "image/png", Files.readAllBytes(path));
            }
            throw new IllegalArgumentException("Wrong image format!");
        } catch (IOException ex) {
            throw new IllegalStateException("Error reading image!");
        }
    }
}
