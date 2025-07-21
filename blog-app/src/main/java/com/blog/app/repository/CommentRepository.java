package com.blog.app.repository;

import com.blog.app.entity.Comment;
import com.blog.app.entity.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPost(Post post);
	List<Comment> findByPostIdAndParentIsNull(Long postId);
	List<Comment> findByParentId(Long parentId);
}
