package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.AkenzhanProjectResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AkenzhanUserMapper.class})
public interface AkenzhanProjectMapper {

    @Mapping(source = "tasks", target = "taskCount", qualifiedByName = "tasksToCount")
    AkenzhanProjectResponse toResponse(AkenzhanProject project);

    @org.mapstruct.Named("tasksToCount")
    default int tasksToCount(java.util.List<?> tasks) {
        return tasks == null ? 0 : tasks.size();
    }
}
