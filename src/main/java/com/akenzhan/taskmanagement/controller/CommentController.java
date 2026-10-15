package com.akenzhan.taskmanagement.controller;

import com.akenzhan.taskmanagement.dto.request.CommentRequest;
import com.akenzhan.taskmanagement.dto.response.CommentResponse;
import com.akenzhan.taskmanagement.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "Add comment to task")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(taskId, request, principal.getName()));
    }

    @GetMapping
    @Operation(summary = "Get all comments for task")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long taskId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId, pageable));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
