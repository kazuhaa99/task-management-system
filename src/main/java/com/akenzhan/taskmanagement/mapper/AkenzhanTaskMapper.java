package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.AkenzhanTaskResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AkenzhanUserMapper.class, AkenzhanTagMapper.class})
public interface AkenzhanTaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    AkenzhanTaskResponse toResponse(AkenzhanTask task);
}
