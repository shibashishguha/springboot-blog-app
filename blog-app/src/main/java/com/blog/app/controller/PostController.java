package com.blog.app.controller;

import com.blog.app.dto.PostDTO;
import com.blog.app.entity.Post;
import com.blog.app.exception.ResourceNotFoundException;
import com.blog.app.repository.PostRepository;
import com.blog.app.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private PostRepository postRepo;
    
    @PostMapping("/users/{userId}/categories/{categoryId}/createimage")
    public ResponseEntity<PostDTO> createPostWithImage(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestPart("post") String postDtoJson,
            @RequestPart(value = "imageUrl", required = false) MultipartFile image
            ) throws IOException {
    	
    	
        ObjectMapper mapper = new ObjectMapper();
        PostDTO postDto = mapper.readValue(postDtoJson, PostDTO.class);
        System.out.println("--------image------"+image);
        PostDTO createdPost = postService.createPost(postDto, image, userId, categoryId);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long postId) {
    	Post post = postRepo.findById(postId)
    	        .orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));

    	    post.setViews(post.getViews() + 1);
    	    postRepo.save(post);

    	    PostDTO dto = new PostDTO();
    	    dto.setId(post.getId());
    	    dto.setTitle(post.getTitle());
    	    dto.setContent(post.getContent());
    	    dto.setViews(post.getViews());

    	    return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @RequestBody PostDTO postDto,
            @PathVariable Long postId) {

        PostDTO updatedPost = postService.updatePost(postDto, postId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDTO>> getPostsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId));
    }
    
    @GetMapping("/trending")
    public ResponseEntity<List<PostDTO>> getTrendingPosts() {
        return ResponseEntity.ok(postService.getTrendingPosts());
    }
}
