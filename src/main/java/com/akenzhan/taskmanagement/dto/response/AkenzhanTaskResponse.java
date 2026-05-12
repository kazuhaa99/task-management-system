package com.akenzhan.taskmanagement.dto.response;

import com.akenzhan.taskmanagement.enums.TaskPriority;
import com.akenzhan.taskmanagement.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AkenzhanTaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long projectId;
    private String projectName;
    private AkenzhanUserResponse assignee;
    private AkenzhanUserResponse creator;
    private Set<AkenzhanTagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
