package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.exception.PostNotFoundException;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.PostRepository;
import com.example.bloggingapp.service.PostService;
import com.example.bloggingapp.service.UserService;
import com.example.bloggingapp.utils.UriSanitizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Set<Post> findByUsername(String username, String authUsername) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authUsername.isEmpty()) {
            return postRepository.findByUser(user);
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (username.equals(authUsername)) {
            return postRepository.findBySelf(authUser);
        }
        return postRepository.findByUserAuth(user, authUser);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Set<Post> findN(int n, String authUsername) {
        if (authUsername.isEmpty()) {
            return postRepository.findN(Limit.of(n));
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        return postRepository.findNAuth(Limit.of(n), authUser);
    }

    @Override
    public boolean isViewable(Post post, String authUsername) {
        User user = post.getUser();
        if (authUsername.isEmpty()) {
            return !post.getHidden() && !post.getDeleted() && !post.getDeletedByAdmin() && !user.getPrivate() && !user.getDeleted() && user.getEnabled();
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.equals(user)) {
            return true;
        }
        return !post.getHidden() && !post.getDeleted() && !post.getDeletedByAdmin() && !user.getPrivate() && !user.getDeleted() && user.getEnabled() && !user.getBlockedUsers().contains(authUser) && !authUser.getBlockedUsers().contains(user);
    }

    @Override
    public String getUriByTitleAndId(String title, Long postId) {
        return UriSanitizer.encode(title + "-" + postId);
    }

    @Override
    public Long getIdByUri(String uri) {
        return Long.parseLong(uri.substring(uri.lastIndexOf('-') + 1));
    }

    @Override
    @Transactional
    public void like(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (post.getLikedBy().contains(authUser)) {
            throw new IllegalStateException("You already liked this post!");
        }
        if (post.getDislikedBy().contains(authUser)) {
            postRepository.removeDislike(authUser.getId(), post.getId());
            postRepository.changeRating(post, 1);
        }
        postRepository.insertLike(authUser.getId(), post.getId());
        postRepository.changeRating(post, 1);
    }

    @Override
    @Transactional
    public void removeLike(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getLikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't liked this post!");
        }
        postRepository.removeLike(authUser.getId(), post.getId());
        postRepository.changeRating(post, -1);
    }

    @Override
    @Transactional
    public void dislike(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (post.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You already disliked this post!");
        }
        if (post.getLikedBy().contains(authUser)) {
            postRepository.removeLike(authUser.getId(), post.getId());
            postRepository.changeRating(post, -1);
        }
        postRepository.insertDislike(authUser.getId(), post.getId());
        postRepository.changeRating(post, -1);
    }


    @Override
    @Transactional
    public void removeDislike(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't disliked this post!");
        }
        postRepository.removeDislike(authUser.getId(), post.getId());
        postRepository.changeRating(post, 1);
    }

    @Override
    public void changeTitle(String authUsername, Long postId, String newTitle) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!authUser.equals(post.getUser())) {
            throw new IllegalStateException("You can only change your own posts!");
        }
        if (post.getDeleted() || post.getDeletedByAdmin()) {
            throw new IllegalStateException("This post is deleted!");
        }
        if (post.getTitle().equals(newTitle)) {
            throw new IllegalArgumentException("New title must be different from the old one!");
        }
        postRepository.changeTitle(post, newTitle);
    }

    @Override
    public void changeContent(String authUsername, Long postId, String newContent) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!authUser.equals(post.getUser())) {
            throw new IllegalStateException("You can only change your own posts!");
        }
        if (post.getDeleted() || post.getDeletedByAdmin()) {
            throw new IllegalStateException("This post is deleted!");
        }
        if (post.getContent().equals(newContent)) {
            throw new IllegalArgumentException("New content must be different from the old one!");
        }
        postRepository.changeContent(post, newContent);
    }

    @Override
    public void tempDelete(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own posts!");
        }
        if (post.getDeleted()) {
            throw new IllegalStateException("This post is already deleted!");
        }
        postRepository.tempDelete(post);
    }

    @Override
    public void undelete(String authUsername, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getDeleted()) {
            throw new IllegalStateException("This post isn't deleted!");
        }
        postRepository.undelete(post);
    }

    @Override
    public void permanentlyDelete(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own posts!");
        }
        postRepository.delete(post);
    }

    @Override
    public void hide(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewable(post, authUsername)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only hide your own posts!");
        }
        if (post.getHidden()) {
            throw new IllegalStateException("This post is already hidden!");
        }
        postRepository.hide(post);
    }

    @Override
    public void unhide(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!post.getUser().equals(authUser)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getHidden()) {
            throw new IllegalStateException("This post is not hidden!");
        }
        postRepository.unhide(post);
    }

    @Override
    public void tempDeleteByAdmin(Long postId) {
        Post post = findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (post.getDeletedByAdmin()) {
            throw new IllegalStateException("This post is already deleted by an admin!");
        }
        postRepository.tempDeleteByAdmin(post);
    }

    @Override
    public void undeleteByAdmin(Long postId) {
        Post post = findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!post.getDeletedByAdmin()) {
            throw new IllegalStateException("This post is not deleted by an admin!");
        }
        postRepository.undeleteByAdmin(post);
    }
}
