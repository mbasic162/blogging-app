package com.example.bloggingapp.controller;


import com.example.bloggingapp.dto.UserLoginDto;
import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.dto.request.RegisterRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.mapper.UserLoginMapper;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.AuthService;
import com.example.bloggingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserLoginMapper userLoginMapper = UserLoginMapper.INSTANCE;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        User user = userService.findByUsername(loginRequest.username()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return ResponseEntity.ok(userLoginMapper.toDto(user, token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserLoginDto> register(@Valid RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        String token = authService.login(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userLoginMapper.toDto(user, token));
    }
}