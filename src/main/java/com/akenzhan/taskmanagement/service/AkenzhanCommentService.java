package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.AkenzhanCommentRequest;
import com.akenzhan.taskmanagement.dto.response.AkenzhanCommentResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanComment;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import com.akenzhan.taskmanagement.exception.AkenzhanResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.AkenzhanCommentMapper;
import com.akenzhan.taskmanagement.repository.AkenzhanCommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AkenzhanCommentService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanCommentService.class);

    private final AkenzhanCommentRepository commentRepository;
    private final AkenzhanCommentMapper commentMapper;
    private final AkenzhanTaskService taskService;
    private final AkenzhanUserService userService;

    public AkenzhanCommentService(AkenzhanCommentRepository commentRepository,
                                   AkenzhanCommentMapper commentMapper,
                                   AkenzhanTaskService taskService,
                                   AkenzhanUserService userService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.taskService = taskService;
        this.userService = userService;
    }

    public AkenzhanCommentResponse addComment(Long taskId, AkenzhanCommentRequest request, String username) {
        AkenzhanUser author = userService.getUserEntityByUsername(username);
        AkenzhanComment comment = AkenzhanComment.builder()
                .content(request.getContent())
                .task(taskService.getTaskEntityById(taskId))
                .author(author)
                .build();
        AkenzhanComment saved = commentRepository.save(comment);
        logger.info("Comment added to task {} by {}", taskId, username);
        return commentMapper.toResponse(saved);
    }

    public Page<AkenzhanCommentResponse> getCommentsByTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskId(taskId, pageable).map(commentMapper::toResponse);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new AkenzhanResourceNotFoundException("Comment not found: " + id);
        }
        commentRepository.deleteById(id);
        logger.info("Comment deleted: {}", id);
    }
}
