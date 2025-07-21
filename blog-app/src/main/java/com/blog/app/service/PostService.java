package com.blog.app.service;

import com.blog.app.dto.PostDTO;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO postDto, Long userId, Long categoryId);
    PostDTO updatePost(PostDTO postDto, Long postId);
    void deletePost(Long postId);
    PostDTO getPostById(Long postId);
    List<PostDTO> getAllPosts();
    List<PostDTO> getPostsByUser(Long userId);
    List<PostDTO> getPostsByCategory(Long categoryId);
    List<PostDTO> getTrendingPosts();
}
