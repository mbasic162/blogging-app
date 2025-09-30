package com.example.bloggingapp.controller;

import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/disable")
    public ResponseEntity<Void> disable(String username) {
        userService.disable(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enable")
    public ResponseEntity<Void> enable(String username) {
        userService.enable(username);
        return ResponseEntity.ok().build();
    }
}
