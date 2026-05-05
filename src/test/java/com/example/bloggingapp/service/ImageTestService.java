package com.example.bloggingapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ImageTestService {

    MultipartFile getImage(Path path);

}