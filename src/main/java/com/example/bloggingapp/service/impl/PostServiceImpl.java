package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.exception.PostNotFoundException;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.Comment;
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

import java.util.Iterator;
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
        Set<Post> posts = postRepository.findByUser(user);
        if (authUsername.isEmpty()) {
            if (!userService.isViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            posts.removeIf(post -> !isViewable(post));
            return posts;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!userService.isViewableAuth(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        posts.removeIf(post -> !isViewableAuth(post, authUser));
        return posts;
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Set<Post> findN(Integer numberOfPosts, String authUsername) {
        Set<Post> posts;
        if (authUsername.isEmpty()) {
            posts = postRepository.findN(Limit.of(numberOfPosts));
            return posts;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        posts = postRepository.findNAuth(Limit.of(numberOfPosts), authUser);
        return posts;
    }

    @Override
    public String getURIByIdAndTitle(Long postId, String title) {
        if (title.length() > 30) {
            title = title.substring(0, 30);
        }
        return UriSanitizer.encode(title + "-" + postId);
    }

    @Override
    public Long getIdByURI(String URI) {
        return Long.parseLong(URI.substring(URI.lastIndexOf('-') + 1));
    }

    @Override
    public Post getPostForViewByURI(String postURI, String authUsername) {
        Post post = findById(getIdByURI(postURI)).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!getURIByIdAndTitle(post.getId(), post.getTitle()).equalsIgnoreCase(postURI)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (authUsername.isEmpty()) {
            if (!isViewable(post)) {
                throw new PostNotFoundException("Post not found!");
            }
            filterComments(post.getComments());
            return post;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!isViewableAuth(post, authUser)) {
            throw new PostNotFoundException("Post not found!");
        }
        filterCommentsAuth(post.getComments(), authUser);
        return post;
    }

    @Override
    @Transactional
    public void like(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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
        if (!isViewableAuth(post, authUser)) {
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

    @Override
    public void filterComments(Set<Comment> comments) {
        for (Iterator<Comment> i = comments.iterator(); i.hasNext(); ) {
            Comment comment = i.next();
            User user = comment.getUser();
            if (comment.getDeleted() || comment.getHidden() || user.getPrivate() || comment.getDeletedByAdmin() || user.getDeleted() || !user.getEnabled()) {
                i.remove();
            } else {
                filterComments(comment.getComments());
            }
        }
    }

    @Override
    public void filterCommentsAuth(Set<Comment> comments, User authUser) {
        for (Iterator<Comment> i = comments.iterator(); i.hasNext(); ) {
            Comment comment = i.next();
            User user = comment.getUser();
            if (!user.equals(authUser) && (comment.getDeleted() || comment.getHidden() || user.getPrivate() || comment.getDeletedByAdmin() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user))) {
                i.remove();
            } else {
                filterCommentsAuth(comment.getComments(), authUser);
            }
        }
    }

    @Override
    public boolean isViewable(Post post) {
        User user = post.getUser();
        return !post.getHidden() && !post.getDeleted() && !post.getDeletedByAdmin() && userService.isViewable(user);
    }

    @Override
    public boolean isViewableAuth(Post post, User authUser) {
        User user = post.getUser();
        if (authUser.equals(user)) {
            return true;
        }
        return !post.getHidden() && !post.getDeleted() && !post.getDeletedByAdmin() && userService.isViewableAuth(user, authUser);
    }
}
