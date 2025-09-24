package com.example.bloggingapp.repository;

import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.user.username = :username")
    Set<Comment> findByUsername(String username);

    @Query("SELECT c FROM Comment c WHERE c.parentPost = :post AND c.parentComment IS NULL ORDER BY c.rating DESC")
    Set<Comment> findByParentPost(Post post);

    @Query("SELECT c FROM Comment c WHERE c.parentComment = :comment ORDER BY c.rating DESC")
    Set<Comment> findByParentComment(Comment comment);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO comment_likes (user_id, comment_id) VALUES (:user_id,:comment_id)", nativeQuery = true)
    void insertLike(@Param("user_id") Long userId, @Param("comment_id") Long commentId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM comment_likes WHERE user_id = :user_id AND comment_id = :comment_id", nativeQuery = true)
    void removeLike(@Param("user_id") Long userId, @Param("comment_id") Long commentId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO comment_dislikes (user_id, comment_id) VALUES (:user_id,:comment_id)", nativeQuery = true)
    void insertDislike(@Param("user_id") Long userId, @Param("comment_id") Long commentId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM comment_dislikes WHERE user_id = :user_id AND comment_id = :comment_id", nativeQuery = true)
    void removeDislike(@Param("user_id") Long userId, @Param("comment_id") Long commentId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment c SET c.rating = c.rating + :change WHERE c = :comment")
    void changeRating(Comment comment, @Param("change") int change);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment c SET c.isDeleted = true WHERE c = :comment")
    void tempDelete(Comment comment);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment c SET c.isDeleted = false WHERE c = :comment")
    void undelete(Comment comment);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment c SET c.isHidden = true WHERE c = :comment")
    void hide(Comment comment);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment c SET c.isHidden = false WHERE c = :comment")
    void unhide(Comment comment);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment c SET c.content=:new_content WHERE c= :comment")
    void changeContent(Comment comment, @Param("new_content") String newContent);
    /*
        @Query("SELECT c FROM Comment c WHERE c.parentPost.id = :postId AND c.isHidden=false AND c.isDeleted=false AND c.user.isPrivate=false AND NOT EXISTS ( SELECT 1 FROM User u JOIN u.blockedUsers bu WHERE u=c.user AND bu=:authUser) AND NOT EXISTS ( SELECT 1 FROM User u2 JOIN u2.blockedUsers bu2 WHERE u2=:authUser AND bu2=c.user) ORDER BY c.rating DESC")
    Set<Comment> findByParentPostIdAuth(Long postId, @Param("authUser") User authUser);
     */
}
