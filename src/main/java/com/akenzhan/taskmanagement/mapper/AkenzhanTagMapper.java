package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.AkenzhanTagResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanTag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AkenzhanTagMapper {
    AkenzhanTagResponse toResponse(AkenzhanTag tag);
}
