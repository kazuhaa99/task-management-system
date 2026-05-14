package com.akenzhan.taskmanagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AkenzhanAttachmentResponse {
    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private Long taskId;
    private AkenzhanUserResponse uploadedBy;
    private LocalDateTime createdAt;
}
