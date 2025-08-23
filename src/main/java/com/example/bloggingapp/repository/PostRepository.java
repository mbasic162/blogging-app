package com.example.bloggingapp.repository;

import com.example.bloggingapp.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.user.username = :username")
    Set<Post> findByUsername(@Param("username") String username);

    @Query(value = "SELECT p FROM Post p WHERE p.isDeleted = false AND p.isHidden = false AND p.user.isPrivate = false ORDER BY p.rating DESC")
    List<Post> findN(Limit n);

    @Query(value = "SELECT p FROM Post p WHERE p.isDeleted = false AND p.isHidden = false AND p.user.isPrivate = false  AND NOT EXISTS ( SELECT 1 FROM User u JOIN u.blockedUsers bu WHERE u=p.user AND bu=:authUser) AND NOT EXISTS ( SELECT 1 FROM User u2 JOIN u2.blockedUsers bu2 WHERE u2=:authUser AND bu2=p.user) ORDER BY p.rating DESC")
    List<Post> findNAuth(Limit n, @Param("auth_id") Long authId);

    @Query(value = "SELECT p.title FROM Post p WHERE p.id=:id")
    Optional<String> getTitleById(@Param("id") Long id);

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
    @Query(value = "UPDATE Post p SET p.rating = p.rating + :change WHERE p.id = :post_id")
    void changeRating(@Param("post_id") Long postId, @Param("change") int change);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isDeleted = true WHERE p.id = :post_id")
    void delete(@Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isDeleted = false WHERE p.id = :post_id")
    void undelete(@Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Post p WHERE p.id = :post_id")
    void permanentlyDelete(@Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isHidden = true WHERE p.id = :post_id")
    void hide(@Param("post_id") Long postId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Post p SET p.isHidden = false WHERE p.id = :post_id")
    void unhide(@Param("post_id") Long postId);
}