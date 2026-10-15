package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.CommentResponse;
import com.akenzhan.taskmanagement.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(source = "task.id", target = "taskId")
    CommentResponse toResponse(Comment comment);
}
