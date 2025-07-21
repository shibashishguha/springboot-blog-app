package com.blog.app.repository;

import com.blog.app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
//	List<Post> findByUserId(Long userId);
    List<Post> findByCategoryId(Long categoryId);
    List<Post> findTop5ByOrderByViewsDesc();
}
