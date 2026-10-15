package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.CommentRequest;
import com.akenzhan.taskmanagement.dto.response.CommentResponse;
import com.akenzhan.taskmanagement.entity.Comment;
import com.akenzhan.taskmanagement.entity.User;
import com.akenzhan.taskmanagement.exception.ResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.CommentMapper;
import com.akenzhan.taskmanagement.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskService taskService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          TaskService taskService,
                          UserService userService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.taskService = taskService;
        this.userService = userService;
    }

    public CommentResponse addComment(Long taskId, CommentRequest request, String username) {
        User author = userService.getUserEntityByUsername(username);
        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(taskService.getTaskEntityById(taskId))
                .author(author)
                .build();
        Comment saved = commentRepository.save(comment);
        logger.info("Comment added to task {} by {}", taskId, username);
        return commentMapper.toResponse(saved);
    }

    public Page<CommentResponse> getCommentsByTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskId(taskId, pageable).map(commentMapper::toResponse);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found: " + id);
        }
        commentRepository.deleteById(id);
        logger.info("Comment deleted: {}", id);
    }
}
