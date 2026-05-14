package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.AkenzhanTaskRequest;
import com.akenzhan.taskmanagement.dto.response.AkenzhanTaskResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanTag;
import com.akenzhan.taskmanagement.entity.AkenzhanTask;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import com.akenzhan.taskmanagement.enums.TaskPriority;
import com.akenzhan.taskmanagement.enums.TaskStatus;
import com.akenzhan.taskmanagement.exception.AkenzhanResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.AkenzhanTaskMapper;
import com.akenzhan.taskmanagement.repository.AkenzhanTagRepository;
import com.akenzhan.taskmanagement.repository.AkenzhanTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AkenzhanTaskService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanTaskService.class);

    private final AkenzhanTaskRepository taskRepository;
    private final AkenzhanTaskMapper taskMapper;
    private final AkenzhanProjectService projectService;
    private final AkenzhanUserService userService;
    private final AkenzhanTagRepository tagRepository;
    private final AkenzhanNotificationService notificationService;

    public AkenzhanTaskService(AkenzhanTaskRepository taskRepository,
                                AkenzhanTaskMapper taskMapper,
                                AkenzhanProjectService projectService,
                                AkenzhanUserService userService,
                                AkenzhanTagRepository tagRepository,
                                AkenzhanNotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectService = projectService;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.notificationService = notificationService;
    }

    public AkenzhanTaskResponse createTask(AkenzhanTaskRequest request, String creatorUsername) {
        AkenzhanUser creator = userService.getUserEntityByUsername(creatorUsername);
        AkenzhanTask task = AkenzhanTask.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .project(projectService.getProjectEntityById(request.getProjectId()))
                .creator(creator)
                .build();

        if (request.getAssigneeId() != null) {
            AkenzhanUser assignee = userService.getUserEntityByUsername(
                    userService.getUserById(request.getAssigneeId()).getUsername());
            task.setAssignee(assignee);
            notificationService.sendTaskAssignmentNotification(assignee.getEmail(), task.getTitle());
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<AkenzhanTag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            task.setTags(tags);
        }

        AkenzhanTask saved = taskRepository.save(task);
        logger.info("Task created: {} by {}", saved.getTitle(), creatorUsername);
        return taskMapper.toResponse(saved);
    }

    public Page<AkenzhanTaskResponse> getTasks(TaskStatus status, TaskPriority priority,
                                            Long projectId, Long assigneeId,
                                            String search, Pageable pageable) {
        return taskRepository.findWithFilters(
                status != null ? status.name() : null,
                priority != null ? priority.name() : null,
                projectId,
                assigneeId,
                (search != null && search.isBlank()) ? null : search,
                pageable)
                .map(taskMapper::toResponse);
    }

    public AkenzhanTaskResponse getTaskById(Long id) {
        AkenzhanTask task = taskRepository.findById(id)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("Task not found: " + id));
        return taskMapper.toResponse(task);
    }

    public AkenzhanTask getTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("Task not found: " + id));
    }

    public AkenzhanTaskResponse updateTask(Long id, AkenzhanTaskRequest request) {
        AkenzhanTask task = getTaskEntityById(id);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            AkenzhanUser assignee = userService.getUserEntityByUsername(
                    userService.getUserById(request.getAssigneeId()).getUsername());
            task.setAssignee(assignee);
        }

        AkenzhanTask updated = taskRepository.save(task);
        logger.info("Task updated: {}", id);
        return taskMapper.toResponse(updated);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new AkenzhanResourceNotFoundException("Task not found: " + id);
        }
        taskRepository.deleteById(id);
        logger.info("Task deleted: {}", id);
    }
}
