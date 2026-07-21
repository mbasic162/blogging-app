package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
import com.example.bloggingapp.enums.ErrorField;
import com.example.bloggingapp.exception.FormException;
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


    @Override
    public User register(RegisterRequest registerRequest) {
        String username = registerRequest.username().trim();
        String email = registerRequest.email().trim();
        if (userService.existsByUsernameIgnoreCase(username)) {
            throw new FormException(ErrorField.USERNAME.getFieldName(), "Username is already in use");
        }
        if (userService.existsByEmailIgnoreCase(email)) {
            throw new FormException(ErrorField.EMAIL.getFieldName(), "Email is already in use");
        }
        String description = registerRequest.description();
        if (description == null) {
            description = "";
        }
        MultipartFile profilePicture = registerRequest.profilePicture();
        String profilePictureName = null;
        if (profilePicture != null) {
            if (!imageService.isValid(profilePicture)) {
                throw new FormException(ErrorField.PROFILE_PICTURE.getFieldName(), "Profile picture is invalid");
            }
            profilePictureName = imageService.save(profilePicture);
        }
        return userService.save(new User(
                username,
                email,
                passwordEncoder.encode(registerRequest.password()),
                description,
                profilePictureName,
                registerRequest.isPrivate()));
    }

    @Override
    public String login(LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.username()).orElseThrow(() -> new UserNotFoundException("Invalid username or password"));
        authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        return jwtUtils.generateToken(user.getUsername(), user.getRoles());
    }

    @Override
    public String login(User user) {
        return jwtUtils.generateToken(user.getUsername(), user.getRoles());
    }
}
