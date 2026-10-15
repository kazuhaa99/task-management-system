package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.UserResponse;
import com.akenzhan.taskmanagement.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
