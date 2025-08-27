package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.exception.CommentNotFoundException;
import com.example.bloggingapp.exception.PostNotFoundException;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.CommentRepository;
import com.example.bloggingapp.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserService userService;
    private final PostService postService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment create(CreateCommentRequest request, String username) {
        Comment comment;
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (request.parentPostId() == null && request.parentCommentId() == null) {
            throw new IllegalArgumentException("Either postId or commentId must be provided!");
        }
        if (request.parentPostId() != null && request.parentCommentId() != null) {
            throw new IllegalArgumentException("Only one of postId or commentId should be provided!");
        }
        if (request.parentPostId() != null) {
            Post post = postService.findById(request.parentPostId()).orElseThrow(() -> new PostNotFoundException("Post not found!"));
            postService.checkAllowViewingAuth(post, username);
            comment = new Comment(request.content(), user, post);
        } else {
            Comment parentComment = findById(request.parentCommentId()).orElseThrow(() -> new CommentNotFoundException("Parent comment not found!"));
            checkAllowViewingAuth(parentComment, username);
            comment = new Comment(request.content(), user, parentComment.getParentPost(), parentComment);
        }
        return save(comment);
    }

    @Override
    public Set<Comment> findByUsername(String username) {
        return commentRepository.findByUsername(username);
    }

    @Override
    public String getUriByIdAndContent(Long commentId, String content) {
        if (content.length() > 15) {
            content = content.substring(0, 15);
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
            if (comment.getDeleted() || comment.getHidden() || comment.getUser().getPrivate()) {
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
            if (comment.getDeleted() || comment.getHidden() || comment.getUser().getPrivate() || comment.getUser().getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(comment.getUser())) {
                i.remove();
            } else {
                filterCommentsAuth(comment.getComments(), authUser);
            }
        }
    }

    @Override
    public void checkAllowViewing(Comment comment) {
        User user = comment.getUser();
        if (comment.getHidden() || comment.getDeleted() || user.getPrivate()) {
            throw new CommentNotFoundException("Comment not found!");
        }
    }

    @Override
    public void checkAllowViewingAuth(Comment comment, String authUsername) {
        User user = comment.getUser();
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if ((comment.getHidden() && !user.equals(authUser)) || comment.getDeleted() || user.getPrivate() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
            throw new CommentNotFoundException("Comment not found!");
        }
    }

    @Override
    public Set<Comment> findByParentPostId(Long postId) {
        Set<Comment> comments = commentRepository.findByParentPostId(postId);
        filterComments(comments);
        return comments;

    }

    @Override
    public Set<Comment> findByParentPostIdAuth(Long postId, String authUsername) {
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Set<Comment> comments = commentRepository.findByParentPostId(postId);
        filterCommentsAuth(comments, authUser);
        return comments;
    }

    @Override
    public Set<Comment> findByParentCommentId(Long commentId) {
        Set<Comment> comments = commentRepository.findByParentCommentId(commentId);
        filterComments(comments);
        return comments;
    }

    @Override
    public Set<Comment> findByParentCommentIdAuth(Long commentId, String username) {
        User authUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Set<Comment> comments = commentRepository.findByParentCommentId(commentId);
        filterCommentsAuth(comments, authUser);
        return comments;
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    @Transactional
    public void like(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (comment.getLikedBy().contains(user)) throw new IllegalStateException("You already liked this comment!");
        if (comment.getDislikedBy().contains(user)) {
            commentRepository.removeDislike(user.getId(), comment.getId());
            commentRepository.changeRating(comment.getId(), 1);
        }
        commentRepository.insertLike(user.getId(), comment.getId());
        commentRepository.changeRating(comment.getId(), 1);
    }

    @Override
    @Transactional
    public void removeLike(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (!comment.getLikedBy().contains(user))
            throw new IllegalStateException("You haven't liked this comment yet!");
        commentRepository.removeLike(user.getId(), comment.getId());
        commentRepository.changeRating(comment.getId(), -1);
    }

    @Override
    @Transactional
    public void dislike(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (comment.getDislikedBy().contains(user))
            throw new IllegalStateException("You already disliked this comment!");
        if (comment.getLikedBy().contains(user)) {
            commentRepository.removeLike(user.getId(), comment.getId());
            commentRepository.changeRating(comment.getId(), -1);
        }
        commentRepository.insertDislike(user.getId(), comment.getId());
        commentRepository.changeRating(comment.getId(), -1);
    }

    @Override
    @Transactional
    public void removeDislike(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (!comment.getDislikedBy().contains(user))
            throw new IllegalStateException("You haven't disliked this comment yet!");
        commentRepository.removeDislike(user.getId(), comment.getId());
        commentRepository.changeRating(comment.getId(), 1);
    }

    @Override
    public void tempDelete(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (user.getBlockedUsers().contains(comment.getUser()) || comment.getUser().getBlockedUsers().contains(user))
            throw new CommentNotFoundException("Comment not found!");
        if (!comment.getUser().equals(user)) throw new IllegalStateException("You can only delete your own comments!");
        if (comment.getDeleted()) throw new IllegalStateException("This comment is already deleted!");
        commentRepository.tempDelete(comment.getId());
    }

    @Override
    public void undelete(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (user.getBlockedUsers().contains(comment.getUser()) || comment.getUser().getBlockedUsers().contains(user))
            throw new CommentNotFoundException("Comment not found!");
        if (!comment.getUser().equals(user))
            throw new IllegalStateException("You can only undelete your own comments!");
        if (!comment.getDeleted()) throw new IllegalStateException("This comment isn't deleted!");
        commentRepository.undelete(comment.getId());
    }

    @Override
    public void permanentlyDelete(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (!comment.getUser().equals(user)) throw new IllegalStateException("You can only delete your own comments!");
        commentRepository.delete(comment);
    }

    @Override
    public void hide(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (!comment.getUser().equals(user)) throw new IllegalStateException("You can only hide your own comments!");
        if (comment.getHidden()) throw new IllegalStateException("This comment is already hidden!");
        commentRepository.hide(comment.getId());
    }

    @Override
    public void unhide(String username, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        checkAllowViewingAuth(comment, username);
        if (!comment.getUser().equals(user)) throw new IllegalStateException("You can only unhide your own posts!");
        if (!comment.getHidden()) throw new IllegalStateException("This comment is not hidden!");
        commentRepository.unhide(comment.getId());
    }
}
