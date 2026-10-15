package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.TaskResponse;
import com.akenzhan.taskmanagement.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TagMapper.class})
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    TaskResponse toResponse(Task task);
}
