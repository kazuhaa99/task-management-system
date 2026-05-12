package com.akenzhan.taskmanagement.dto.response;

import com.akenzhan.taskmanagement.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AkenzhanUserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
