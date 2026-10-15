package com.akenzhan.taskmanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private UserResponse owner;
    private int taskCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
