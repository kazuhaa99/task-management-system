package com.akenzhan.taskmanagement.dto.request;

import com.akenzhan.taskmanagement.enums.TaskPriority;
import com.akenzhan.taskmanagement.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class AkenzhanTaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskStatus status = TaskStatus.TODO;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long assigneeId;

    private Set<Long> tagIds;
}
