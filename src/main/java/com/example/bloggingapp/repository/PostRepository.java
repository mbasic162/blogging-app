package com.example.bloggingapp.repository;

import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT p FROM Post p WHERE p.user.username=:username AND p.isDeleted = false AND p.isHidden = false AND p.user.isPrivate = false AND p.user.isEnabled = true")
    Set<Post> findByUsername(String username);

    @Query(value = "SELECT p FROM Post p WHERE p.user.username=:username AND p.isDeleted = false AND p.isHidden = false AND p.user.isPrivate = false AND p.user.isEnabled = true AND NOT EXISTS ( SELECT 1 FROM User u JOIN u.blockedUsers bu WHERE u=p.user AND bu.id=:auth_id) AND NOT EXISTS ( SELECT 1 FROM User u2 JOIN u2.blockedUsers bu2 WHERE u2.id=:auth_id AND bu2=p.user)")
    Set<Post> findByUsernameAuth(String username, @Param("auth_id") Long authId);

    @Query(value = "SELECT p FROM Post p WHERE p.isDeleted = false AND p.isHidden = false AND p.user.isPrivate = false AND p.user.isEnabled = true ORDER BY p.rating DESC")
    Set<Post> findN(Limit n);

    @Query(value = "SELECT p FROM Post p WHERE p.isDeleted = false AND p.isHidden = false AND p.user.isPrivate = false AND p.user.isEnabled = true AND NOT EXISTS ( SELECT 1 FROM User u JOIN u.blockedUsers bu WHERE u=p.user AND bu=:auth_user) AND NOT EXISTS ( SELECT 1 FROM User u2 JOIN u2.blockedUsers bu2 WHERE u2=:auth_user AND bu2=p.user) ORDER BY p.rating DESC")
    Set<Post> findNAuth(Limit n, @Param("auth_user") User authUser);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_likes (user_id, post_id) VALUES (:user_id,:post_id)", nativeQuery = true)
    void insertLike(@Param("user_id") Long userId, @Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM post_likes WHERE user_id = :user_id AND post_id = :post_id", nativeQuery = true)
    void removeLike(@Param("user_id") Long userId, @Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_dislikes (user_id, post_id) VALUES (:user_id,:post_id)", nativeQuery = true)
    void insertDislike(@Param("user_id") Long userId, @Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM post_dislikes WHERE user_id = :user_id AND post_id = :post_id", nativeQuery = true)
    void removeDislike(@Param("user_id") Long userId, @Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.rating = p.rating + :change WHERE p = :post")
    void changeRating(Post post, int change);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.title=:new_title WHERE p=:post")
    void changeTitle(Post post, @Param("new_title") String newTitle);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.content=:new_content WHERE p=:post")
    void changeContent(Post post, @Param("new_content") String newContent);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isDeleted = true WHERE p = :post")
    void tempDelete(Post post);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isDeleted = false WHERE p = :post")
    void undelete(Post post);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isHidden = true WHERE p = :post")
    void hide(Post post);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isHidden = false WHERE p = :post")
    void unhide(Post post);
}