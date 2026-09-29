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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserLoginMapper userLoginMapper;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginDto> login(@RequestBody @Valid LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.username()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        String token = authService.login(loginRequest, user);
        return ResponseEntity.ok(userLoginMapper.toDto(user, token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserLoginDto> register(@Valid RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        String token = authService.login(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userLoginMapper.toDto(user, token));
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}