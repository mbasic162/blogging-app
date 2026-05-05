package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.security.JwtUtils;
import com.example.bloggingapp.service.AuthService;
import com.example.bloggingapp.service.ImageService;
import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final ImageService imageService;

    public User register(RegisterRequest registerRequest) {
        if (userService.existsByUsernameIgnoreCase(registerRequest.username())) {
            throw new IllegalArgumentException("Username is already in use!");
        }
        if (userService.existsByEmailIgnoreCase(registerRequest.email())) {
            throw new IllegalArgumentException("Email is already in use!");
        }
        String description = registerRequest.description();
        if (description == null) {
            description = "";
        }
        MultipartFile profilePicture = registerRequest.profilePicture();
        String profilePictureName;
        if (profilePicture == null) {
            profilePictureName = "default.jpg";
        } else {
            if (!imageService.isValid(profilePicture)) {
                throw new IllegalArgumentException("Profile picture is invalid!");
            }
            profilePictureName = imageService.save(profilePicture);
        }
        return userService.save(new User(
                registerRequest.username(),
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()),
                description,
                profilePictureName,
                registerRequest.isPrivate()));
    }

    public String login(LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.username()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        return jwtUtils.generateToken(user.getUsername(), user.getRoles());
    }
}
