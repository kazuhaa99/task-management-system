package com.akenzhan.taskmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AkenzhanCommentRequest {

    @NotBlank(message = "Content is required")
    private String content;
}
