package com.blog.app.service.impl;

import com.blog.app.dto.PostDTO;
import com.blog.app.entity.Category;
import com.blog.app.entity.Post;
import com.blog.app.entity.User;
import com.blog.app.exception.ResourceNotFoundException;
import com.blog.app.repository.CategoryRepository;
import com.blog.app.repository.PostRepository;
import com.blog.app.repository.UserRepository;
import com.blog.app.service.CloudinaryService;
import com.blog.app.service.PostService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
	
	private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public PostDTO createPost(PostDTO postDto,MultipartFile image,Long userId, Long categoryId) throws java.io.IOException {
    	 logger.info("Creating post for user ID: {} in category ID: {}", userId, categoryId);
    	 User user = userRepo.findById(userId)
                 .orElseThrow(() -> {
                     logger.warn("User with ID {} not found", userId);
                     return new ResourceNotFoundException("User", "ID", userId);
                 });

         Category category = categoryRepo.findById(categoryId)
                 .orElseThrow(() -> {
                     logger.warn("Category with ID {} not found", categoryId);
                     return new ResourceNotFoundException("Category", "ID", categoryId);
                 });


        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setCategory(category);
        
        if (image != null && !image.isEmpty()) {
        	logger.debug("Uploading image for post: {}", postDto.getTitle());
            String imageUrl = cloudinaryService.uploadImage(image);
            post.setImageUrl(imageUrl);
            logger.info("Image uploaded successfully: {}", imageUrl);
        }
        Post savedPost = postRepo.save(post);
        logger.info("Post '{}' created successfully with ID: {}", savedPost.getTitle(), savedPost.getId());
        return mapToDTO(savedPost);
    }

    @Override
    public PostDTO updatePost(PostDTO postDto, Long postId) {
    	logger.info("Updating post with ID: {}", postId);
    	Post post = postRepo.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post with ID {} not found for update", postId);
                    return new ResourceNotFoundException("Post", "ID", postId);
                });

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        Post updatedPost = postRepo.save(post);
        logger.info("Post with ID {} updated successfully", postId);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePost(Long postId) {
    	logger.info("Deleting post with ID: {}", postId);
    	 Post post = postRepo.findById(postId)
                 .orElseThrow(() -> {
                     logger.warn("Post with ID {} not found for deletion", postId);
                     return new ResourceNotFoundException("Post", "ID", postId);
                 });
        postRepo.delete(post);
        logger.info("Post with ID {} deleted successfully", postId);
    }

    @Override
    public PostDTO getPostById(Long postId) {
    	logger.info("Fetching post with ID: {}", postId);
    	Post post = postRepo.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post with ID {} not found", postId);
                    return new ResourceNotFoundException("Post", "ID", postId);
                });

        logger.debug("Post found: {}", post.getTitle());
        return mapToDTO(post);
    }

    @Override
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepo.findAll();
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getPostsByUser(Long userId) {
//        List<Post> posts = postRepo.findByUserId(userId);
//        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    	return null;
    }

    @Override
    public List<PostDTO> getPostsByCategory(Long categoryId) {
    	logger.info("Fetching posts by category ID: {}", categoryId);
        List<Post> posts = postRepo.findByCategoryId(categoryId);
        logger.debug("Total posts found in category {}: {}", categoryId, posts.size());
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PostDTO mapToDTO(Post post) {
    	logger.debug("Mapping Post entity to PostDTO for post ID: {}", post.getId());
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUserId(post.getUser().getUserId());
        dto.setCategoryId(post.getCategory().getId());
        dto.setImageUrl(post.getImageUrl());
        return dto;
    }
    
    @Override
    public List<PostDTO> getTrendingPosts() {
    	logger.info("Fetching trending posts");
        List<Post> trendingPosts = postRepo.findTop5ByOrderByViewsDesc();
        logger.debug("Top trending posts found: {}", trendingPosts.size());
        List<PostDTO> trendingDtos = new ArrayList<>();

        for (Post post : trendingPosts) {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setViews(post.getViews());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setUpdatedAt(post.getUpdatedAt());
            dto.setUserId(post.getUser().getUserId());
            dto.setCategoryId(post.getCategory().getId());
            trendingDtos.add(dto);
        }

        return trendingDtos;
    }
}
