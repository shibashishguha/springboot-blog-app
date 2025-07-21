package com.blog.app.service;

import java.util.List;

import com.blog.app.dto.CommentDTO;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDto, Long postId);
    void deleteComment(Long commentId);
    CommentDTO addComment(CommentDTO commentDto);
    List<CommentDTO> getCommentsByPostId(Long postId);
}
