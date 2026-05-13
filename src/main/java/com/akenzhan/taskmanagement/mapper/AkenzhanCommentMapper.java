package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.AkenzhanCommentResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AkenzhanUserMapper.class})
public interface AkenzhanCommentMapper {

    @Mapping(source = "task.id", target = "taskId")
    AkenzhanCommentResponse toResponse(AkenzhanComment comment);
}
