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
import com.example.bloggingapp.service.helper.ViewHelper;
import com.example.bloggingapp.utils.UriSanitizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final ViewHelper viewHelper;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment create(CreateCommentRequest request, User authUser) {
        Comment comment;
        if (request.parentPostId() == null && request.parentCommentId() == null) {
            throw new IllegalArgumentException("Either parent post or parent comment must be provided!");
        }
        if (request.parentPostId() != null && request.parentCommentId() != null) {
            throw new IllegalArgumentException("Only one of parent post or parent comment should be provided!");
        }
        if (request.parentPostId() != null) {
            Post post = postService.findById(request.parentPostId()).orElseThrow(() -> new PostNotFoundException("Post not found!"));
            if (!viewHelper.isPostViewable(post, authUser)) {
                throw new PostNotFoundException("Post not found!");
            }
            comment = new Comment(request.content(), authUser, post);
        } else {
            Comment parentComment = findById(request.parentCommentId()).orElseThrow(() -> new CommentNotFoundException("Parent comment not found!"));
            if (!viewHelper.isCommentViewable(parentComment, authUser)) {
                throw new CommentNotFoundException("Parent comment not found!");
            }
            comment = new Comment(request.content(), authUser, parentComment.getParentPost(), parentComment);
        }
        return save(comment);
    }

    @Override
    public Set<Comment> findByUser(User user, User authUser) {
        Set<Comment> comments = commentRepository.findByUser(user);
        if (authUser == null) {
            if (!viewHelper.isUserViewable(user)) {
                throw new UserNotFoundException("User not found!");
            }
            viewHelper.filterComments(comments);
            return comments;
        }
        if (!viewHelper.isUserViewable(user, authUser)) {
            throw new UserNotFoundException("User not found!");
        }
        viewHelper.filterComments(comments, authUser);
        return comments;
    }

    @Override
    public String getURIByIdAndContent(Long commentId, String content) {
        if (content.length() > 30 && content.charAt(30) != ' ') {
            content = content.substring(0, 31);
        } else if (content.length() > 30) {
            content = content.substring(0, 30);
        }
        return UriSanitizer.encode(content + "-" + commentId);
    }

    @Override
    public Long getIdByURI(String commentURI) {
        if (!existsByURI(commentURI)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        return Long.parseLong(commentURI.substring(commentURI.lastIndexOf('-') + 1));
    }

    @Override
    public Set<Comment> findByParentPost(String postURI, User authUser) {
        Post post = postService.findById(postService.getIdByURI(postURI)).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        Set<Comment> comments = commentRepository.findByParentPost(post);
        if (authUser == null) {
            if (!viewHelper.isPostViewable(post)) {
                throw new PostNotFoundException("Post not found!");
            }
            viewHelper.filterComments(comments);
            return comments;
        }
        if (!viewHelper.isPostViewable(post, authUser)) {
            throw new PostNotFoundException("Post not found!");
        }
        viewHelper.filterComments(comments, authUser);
        return comments;
    }

    @Override
    public Set<Comment> findByParentComment(String commentURI, User authUser) {
        Comment parentComment = findById(getIdByURI(commentURI)).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        Set<Comment> comments = commentRepository.findByParentComment(parentComment);
        if (authUser == null) {
            if (!viewHelper.isCommentViewable(parentComment)) {
                throw new CommentNotFoundException("Comment not found!");
            }
            viewHelper.filterComments(comments);
            return comments;
        }
        if (!viewHelper.isCommentViewable(parentComment, authUser)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        viewHelper.filterComments(comments, authUser);
        return comments;
    }

    @Override
    public Comment getCommentForViewByURI(String commentURI, User authUser) {
        Comment comment = findById(getIdByURI(commentURI)).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (authUser == null) {
            if (!viewHelper.isCommentViewable(comment)) {
                throw new CommentNotFoundException("Comment not found!");
            }
            return comment;
        }
        if (!viewHelper.isCommentViewable(comment, authUser)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        return comment;
    }

    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public Integer getViewableCommentCountByPost(Post post, User authUser) {
        if (authUser == null) {
            return commentRepository.getViewableCommentCountByPost(post);
        }
        return commentRepository.getViewableCommentCountByPostAuth(post, authUser);
    }

    @Override
    public boolean existsByURI(String commentURI) {
        Long id = Long.parseLong(commentURI.substring(commentURI.lastIndexOf('-') + 1));
        Comment comment = findById(id).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        return getURIByIdAndContent(comment.getId(), comment.getContent()).equalsIgnoreCase(commentURI);
    }

    @Override
    @Transactional
    public void like(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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
    public void removeLike(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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
    public void dislike(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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
    public void removeDislike(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't disliked this comment!");
        }
        commentRepository.removeDislike(authUser.getId(), comment.getId());
        commentRepository.changeRating(comment, 1);
    }

    @Override
    public void tempDelete(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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
    public void undelete(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getDeleted()) {
            throw new IllegalStateException("This comment isn't deleted!");
        }
        commentRepository.undelete(comment);
    }

    @Override
    public void permanentlyDelete(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!comment.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own comments!");
        }
        commentRepository.delete(comment);
    }

    @Override
    public void hide(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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
    public void unhide(Long commentId, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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
    public void changeContent(Long commentId, String authUsername, String newContent) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!viewHelper.isCommentViewable(comment, authUser)) {
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