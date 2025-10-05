package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.exception.CommentNotFoundException;
import com.example.bloggingapp.exception.PostNotFoundException;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.CommentRepository;
import com.example.bloggingapp.service.CommentService;
import com.example.bloggingapp.service.PostService;
import com.example.bloggingapp.service.UserService;
import com.example.bloggingapp.utils.UriSanitizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment create(CreateCommentRequest request, String authUsername) {
        Comment comment;
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (request.parentPostId() == null && request.parentCommentId() == null) {
            throw new IllegalArgumentException("Either parent post or parent comment must be provided!");
        }
        if (request.parentPostId() != null && request.parentCommentId() != null) {
            throw new IllegalArgumentException("Only one of parent post or parent comment should be provided!");
        }
        if (request.parentPostId() != null) {
            Post post = postService.findById(request.parentPostId()).orElseThrow(() -> new PostNotFoundException("Post not found!"));
            if (!postService.isViewable(post, authUsername)) {
                throw new PostNotFoundException("Post not found!");
            }
            comment = new Comment(request.content(), authUser, post);
        } else {
            Comment parentComment = findById(request.parentCommentId()).orElseThrow(() -> new CommentNotFoundException("Parent comment not found!"));
            if (!isViewable(parentComment, authUsername)) {
                throw new CommentNotFoundException("Parent comment not found!");
            }
            comment = new Comment(request.content(), authUser, parentComment.getParentPost(), parentComment);
        }
        return save(comment);
    }

    @Override
    public Set<Comment> findByUsername(String username) {
        return commentRepository.findByUsername(username);
    }

    @Override
    public String getUriByIdAndContent(Long commentId, String content) {
        if (content.length() > 14 && content.charAt(14) != ' ') {
            content = content.substring(0, 15);
        } else if (content.length() > 14) {
            content = content.substring(0, 14);
        }
        return UriSanitizer.encode(content + "-" + commentId);
    }

    @Override
    public Long getIdByUri(String uri) {
        return Long.parseLong(uri.substring(uri.lastIndexOf('-') + 1));
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
            if (comment.getDeleted() || comment.getHidden() || user.getPrivate() || comment.getDeletedByAdmin() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
                i.remove();
            } else {
                filterCommentsAuth(comment.getComments(), authUser);
            }
        }
    }

    @Override
    public boolean isViewable(Comment comment, String authUsername) {
        User user = comment.getUser();
        if (authUsername.isEmpty()) {
            return !comment.getHidden() && !comment.getDeleted() && !comment.getDeletedByAdmin() && !user.getPrivate() && !user.getDeleted() && user.getEnabled();
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (authUser.equals(user)) {
            return true;
        }
        return !comment.getHidden() && !comment.getDeleted() && !comment.getDeletedByAdmin() && !user.getPrivate() && user.getEnabled() && !user.getBlockedUsers().contains(authUser) && !authUser.getBlockedUsers().contains(user);
    }

    @Override
    public Set<Comment> findByParentPost(Post post, String authUsername) {
        Set<Comment> comments = commentRepository.findByParentPost(post);
        if (authUsername.isEmpty()) {
            filterComments(comments);
            return comments;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        filterCommentsAuth(comments, authUser);
        return comments;

    }

    @Override
    public Set<Comment> findByParentComment(Comment comment, String authUsername) {
        Set<Comment> comments = commentRepository.findByParentComment(comment);
        if (authUsername.isEmpty()) {
            filterComments(comments);
            return comments;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        filterCommentsAuth(comments, authUser);
        return comments;
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    @Transactional
    public void like(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (comment.getLikedBy().contains(authUser)) {
            throw new IllegalStateException("You already liked this comment!");
        }
        if (comment.getDislikedBy().contains(authUser)) {
            commentRepository.removeDislike(authUser.getId(), comment.getId());
            commentRepository.changeRating(comment, 1);
        }
        commentRepository.insertLike(authUser.getId(), comment.getId());
        commentRepository.changeRating(comment, 1);
    }

    @Override
    @Transactional
    public void removeLike(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getLikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't liked this comment!");
        }
        commentRepository.removeLike(authUser.getId(), comment.getId());
        commentRepository.changeRating(comment, -1);
    }

    @Override
    @Transactional
    public void dislike(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (comment.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You already disliked this comment!");
        }
        if (comment.getLikedBy().contains(authUser)) {
            commentRepository.removeLike(authUser.getId(), comment.getId());
            commentRepository.changeRating(comment, -1);
        }
        commentRepository.insertDislike(authUser.getId(), comment.getId());
        commentRepository.changeRating(comment, -1);
    }

    @Override
    @Transactional
    public void removeDislike(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't disliked this comment!");
        }
        commentRepository.removeDislike(authUser.getId(), comment.getId());
        commentRepository.changeRating(comment, 1);
    }

    @Override
    public void tempDelete(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own comments!");
        }
        if (comment.getDeleted()) {
            throw new IllegalStateException("This comment is already deleted!");
        }
        commentRepository.tempDelete(comment);
    }

    @Override
    public void undelete(String authUsername, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getDeleted()) {
            throw new IllegalStateException("This comment isn't deleted!");
        }
        commentRepository.undelete(comment);
    }

    @Override
    public void permanentlyDelete(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own comments!");
        }
        commentRepository.delete(comment);
    }

    @Override
    public void hide(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only hide your own comments!");
        }
        if (comment.getHidden()) {
            throw new IllegalStateException("This comment is already hidden!");
        }
        commentRepository.hide(comment);
    }

    @Override
    public void unhide(String authUsername, Long commentId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only unhide your own posts!");
        }
        if (!comment.getHidden()) {
            throw new IllegalStateException("This comment is not hidden!");
        }
        commentRepository.unhide(comment);
    }

    @Override
    public void changeContent(String authUsername, Long commentId, String newContent) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only change your own comments!");
        }
        if (comment.getContent().equals(newContent)) {
            throw new IllegalArgumentException("New content must be different from the old one!");
        }
        commentRepository.changeContent(comment, newContent);
    }

    @Override
    public void tempDeleteByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (comment.getDeletedByAdmin()) {
            throw new IllegalStateException("This comment is already deleted by an admin!");
        }
        commentRepository.tempDeleteByAdmin(comment);
    }

    @Override
    public void undeleteByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!comment.getDeletedByAdmin()) {
            throw new IllegalStateException("This comment is not deleted by an admin!");
        }
        commentRepository.undeleteByAdmin(comment);
    }
}