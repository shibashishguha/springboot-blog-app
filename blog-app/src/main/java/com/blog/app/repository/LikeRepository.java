package com.blog.app.repository;

import com.blog.app.entity.Like;
import com.blog.app.entity.Post;
import com.blog.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    Long countByPost(Post post);
    void deleteByUserAndPost(User user, Post post);
}
