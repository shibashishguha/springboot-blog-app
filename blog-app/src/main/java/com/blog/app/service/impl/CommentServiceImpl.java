package com.blog.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.app.dto.CommentDTO;
import com.blog.app.entity.Comment;
import com.blog.app.entity.Post;
import com.blog.app.exception.ResourceNotFoundException;
import com.blog.app.repository.CommentRepository;
import com.blog.app.repository.PostRepository;
import com.blog.app.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CommentServiceImpl implements CommentService {
	
	 private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private PostRepository postRepo;

    @Override
    public CommentDTO createComment(CommentDTO commentDto, Long postId) {
    	logger.info("Creating comment on postId: {}", postId);
    	Post post = postRepo.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id: {}", postId);
                    return new ResourceNotFoundException("Post", "id", postId);
                });

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        if (commentDto.getParentId() != null) {
            logger.info("Fetching parent comment with id: {}", commentDto.getParentId());
            Comment parent = commentRepo.findById(commentDto.getParentId())
                    .orElseThrow(() -> {
                        logger.error("Parent comment not found with id: {}", commentDto.getParentId());
                        return new ResourceNotFoundException("Comment", "id", commentDto.getParentId());
                    });
            comment.setParent(parent);
        }

        Comment savedComment = commentRepo.save(comment);
        logger.info("Comment created with id: {}", savedComment.getId());
        return convertToDto(savedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
    	logger.info("Deleting comment with id: {}", commentId);
    	Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment not found with id: {}", commentId);
                    return new ResourceNotFoundException("Comment", "id", commentId);
                });
        commentRepo.delete(comment);
        logger.info("Comment with id {} deleted successfully", commentId);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
    	logger.info("Fetching comments for postId: {}", postId);
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id: {}", postId);
                    return new ResourceNotFoundException("Post", "id", postId);
                });

        List<Comment> allComments = commentRepo.findByPost(post);
        logger.debug("Found {} comments for postId {}", allComments.size(), postId);
        // Filter top-level comments (those with no parent)
        List<Comment> topLevelComments = allComments.stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());

        // Convert recursively
        return topLevelComments.stream()
                .map(this::convertToDtoWithReplies)
                .collect(Collectors.toList());
    }

    // Recursive method to map replies
    private CommentDTO convertToDtoWithReplies(Comment comment) {
        CommentDTO dto = convertToDto(comment);

        List<CommentDTO> replyDtos = comment.getReplies().stream()
                .map(this::convertToDtoWithReplies)
                .collect(Collectors.toList());

        dto.setReplies(replyDtos);
        return dto;
    }

    private CommentDTO convertToDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setId(comment.getPost().getId());

        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
        }

        return dto;
    }

    @Override
    public CommentDTO addComment(CommentDTO commentDto) {
    	logger.info("Adding comment via addComment(), postId: {}", commentDto.getId());
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Post post = postRepo.findById(commentDto.getId())
                .orElseThrow(() -> {
                    logger.error("Post not found with id: {}", commentDto.getId());
                    return new ResourceNotFoundException("Post", "id", commentDto.getId());
                });
        comment.setPost(post);

        if (commentDto.getParentId() != null) {
            logger.info("Adding comment as reply to parentId: {}", commentDto.getParentId());
            Comment parent = commentRepo.findById(commentDto.getParentId())
                    .orElseThrow(() -> {
                        logger.error("Parent comment not found with id: {}", commentDto.getParentId());
                        return new ResourceNotFoundException("Comment", "id", commentDto.getParentId());
                    });
            comment.setParent(parent);
        }

        Comment saved = commentRepo.save(comment);
        logger.info("Comment saved successfully with id: {}", saved.getId());
        return convertToDtoWithReplies(saved);
    }

	
}