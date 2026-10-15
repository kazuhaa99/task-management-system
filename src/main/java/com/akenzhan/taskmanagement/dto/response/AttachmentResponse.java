package com.akenzhan.taskmanagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttachmentResponse {
    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private Long taskId;
    private UserResponse uploadedBy;
    private LocalDateTime createdAt;
}
