package com.blog.app.controller;

import com.blog.app.service.LikeService;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> likePost(@PathVariable Long postId, Principal principal) {
        String message = likeService.likePost(postId, principal.getName());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId, Principal principal) {
        likeService.unlikePost(postId, principal.getName());
        return new ResponseEntity<>("Post unliked successfully", HttpStatus.OK); 
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        Long count = likeService.countLikes(postId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
