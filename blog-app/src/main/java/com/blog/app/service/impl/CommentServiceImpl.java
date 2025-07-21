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

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private PostRepository postRepo;

    @Override
    public CommentDTO createComment(CommentDTO commentDto, Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        if (commentDto.getParentId() != null) {
            Comment parent = commentRepo.findById(commentDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDto.getParentId()));
            comment.setParent(parent);
        }

        Comment savedComment = commentRepo.save(comment);
        return convertToDto(savedComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        commentRepo.delete(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<Comment> allComments = commentRepo.findByPost(post);

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
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        // Set Post
        Post post = postRepo.findById(commentDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", commentDto.getId()));
        comment.setPost(post);

        // Set Parent Comment if it's a reply
        if (commentDto.getParentId() != null) {
            Comment parent = commentRepo.findById(commentDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDto.getParentId()));
            comment.setParent(parent);
        }

        Comment saved = commentRepo.save(comment);
        return convertToDtoWithReplies(saved); // include nested replies if needed
    }

	
}