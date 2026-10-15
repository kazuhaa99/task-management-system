package com.akenzhan.taskmanagement.mapper;

import com.akenzhan.taskmanagement.dto.response.ProjectResponse;
import com.akenzhan.taskmanagement.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMapper {

    @Mapping(source = "tasks", target = "taskCount", qualifiedByName = "tasksToCount")
    ProjectResponse toResponse(Project project);

    @org.mapstruct.Named("tasksToCount")
    default int tasksToCount(java.util.List<?> tasks) {
        return tasks == null ? 0 : tasks.size();
    }
}
