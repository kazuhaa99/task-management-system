package com.akenzhan.taskmanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AkenzhanProjectResponse {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private AkenzhanUserResponse owner;
    private int taskCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
