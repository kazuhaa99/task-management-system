package com.akenzhan.taskmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AkenzhanProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;
}
