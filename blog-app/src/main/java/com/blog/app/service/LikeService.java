package com.blog.app.service;

public interface LikeService {
    void likePost(Long postId, Long userId);
    String likePost(Long postId, String email); 
    void unlikePost(Long postId, String email);
    void unlikePost(Long postId, Long userId);
    Long countLikes(Long postId);
}
