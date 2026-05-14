package com.akenzhan.taskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AkenzhanAuthResponse {
    private String token;
    private String username;
    private String role;
}
