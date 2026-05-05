package com.example.bloggingapp.service;

import org.springframework.web.multipart.MultipartFile;


public interface ImageService {
    String save(MultipartFile image);

    boolean isValid(MultipartFile image);
}
