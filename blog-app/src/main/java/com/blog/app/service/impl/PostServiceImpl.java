package com.blog.app.service.impl;

import com.blog.app.dto.PostDTO;
import com.blog.app.entity.Category;
import com.blog.app.entity.Post;
import com.blog.app.entity.User;
import com.blog.app.exception.ResourceNotFoundException;
import com.blog.app.repository.CategoryRepository;
import com.blog.app.repository.PostRepository;
import com.blog.app.repository.UserRepository;
import com.blog.app.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public PostDTO createPost(PostDTO postDto, Long userId, Long categoryId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", categoryId));

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImage(postDto.getImage());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setCategory(category);

        Post savedPost = postRepo.save(post);
        return mapToDTO(savedPost);
    }

    @Override
    public PostDTO updatePost(PostDTO postDto, Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImage(postDto.getImage());
        post.setUpdatedAt(LocalDateTime.now());

        return mapToDTO(postRepo.save(post));
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        postRepo.delete(post);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
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
        List<Post> posts = postRepo.findByCategoryId(categoryId);
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PostDTO mapToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImage(post.getImage());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUserId(post.getUser().getUserId());
        dto.setCategoryId(post.getCategory().getId());
        return dto;
    }
    
    @Override
    public List<PostDTO> getTrendingPosts() {
        List<Post> trendingPosts = postRepo.findTop5ByOrderByViewsDesc();
        List<PostDTO> trendingDtos = new ArrayList<>();

        for (Post post : trendingPosts) {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setViews(post.getViews());
            dto.setImage(post.getImage());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setUpdatedAt(post.getUpdatedAt());
            dto.setUserId(post.getUser().getUserId());
            dto.setCategoryId(post.getCategory().getId());
            trendingDtos.add(dto);
        }

        return trendingDtos;
    }
}
