package com.example.bloggingapp.controller;

import com.example.bloggingapp.service.CommentService;
import com.example.bloggingapp.service.PostService;
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
    private final PostService postService;
    private final CommentService commentService;

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

    @PostMapping("/deletePost")
    public ResponseEntity<Void> deletePost(Long postId) {
        postService.tempDeleteByAdmin(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undeletePost")
    public ResponseEntity<Void> undeletePost(Long postId) {
        postService.undeleteByAdmin(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<Void> deleteComment(Long commentId) {
        commentService.tempDeleteByAdmin(commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undeleteComment")
    public ResponseEntity<Void> undeleteComment(Long commentId) {
        commentService.undeleteByAdmin(commentId);
        return ResponseEntity.ok().build();
    }
}