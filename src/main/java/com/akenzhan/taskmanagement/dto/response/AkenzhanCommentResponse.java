package com.akenzhan.taskmanagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AkenzhanCommentResponse {
    private Long id;
    private String content;
    private AkenzhanUserResponse author;
    private Long taskId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
