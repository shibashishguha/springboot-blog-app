package com.blog.app.service.impl;

import com.blog.app.entity.Like;
import com.blog.app.entity.Post;
import com.blog.app.entity.User;
import com.blog.app.repository.LikeRepository;
import com.blog.app.repository.PostRepository;
import com.blog.app.repository.UserRepository;
import com.blog.app.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
	
	private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void likePost(Long postId, Long userId) {
    	logger.info("Attempting to like post. Post ID: {}, User ID: {}", postId, userId);
    	Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new RuntimeException("Post not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });
        Optional<Like> existing = likeRepository.findByUserAndPost(user, post);
        if (existing.isPresent()) {
        	logger.warn("User {} has already liked post {}", userId, postId);
            throw new RuntimeException("You have already liked this post");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);
        logger.info("Post {} liked by user {}", postId, userId);
    }

    public String likePost(Long postId, String email) {
    	logger.info("Attempting to like post using email. Post ID: {}, Email: {}", postId, email);
    	User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });
        this.likePost(postId, user.getUserId());
        return "Post liked successfully";
    }

    @Override
    public void unlikePost(Long postId, Long userId) {
    	logger.info("Attempting to unlike post. Post ID: {}, User ID: {}", postId, userId);
    	Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new RuntimeException("Post not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });

        likeRepository.deleteByUserAndPost(user, post);
        logger.info("Post {} unliked by user {}", postId, userId);
    }
    
    

    @Override
    public Long countLikes(Long postId) {
    	logger.info("Counting likes for post ID: {}", postId);
    	 Post post = postRepository.findById(postId)
                 .orElseThrow(() -> {
                     logger.error("Post not found with ID: {}", postId);
                     return new RuntimeException("Post not found");
                 });
         Long count = likeRepository.countByPost(post);
         logger.info("Post {} has {} likes", postId, count);
         return count;
    }

	@Override
	public void unlikePost(Long postId, String username) {
		// TODO Auto-generated method stub
		
	}
}
