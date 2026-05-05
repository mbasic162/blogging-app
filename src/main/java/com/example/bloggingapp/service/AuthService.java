package com.example.bloggingapp.service;

import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
import com.example.bloggingapp.model.User;

public interface AuthService {

    User register(RegisterRequest registerRequest);

    String login(LoginRequest loginRequest);
}
