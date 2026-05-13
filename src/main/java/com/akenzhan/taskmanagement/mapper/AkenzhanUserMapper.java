package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.AkenzhanUserResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AkenzhanUserMapper {
    AkenzhanUserResponse toResponse(AkenzhanUser user);
}
