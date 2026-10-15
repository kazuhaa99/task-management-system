package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.TagResponse;
import com.akenzhan.taskmanagement.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);
}
