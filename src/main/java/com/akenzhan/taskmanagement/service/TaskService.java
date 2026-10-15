package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.TaskRequest;
import com.akenzhan.taskmanagement.dto.response.TaskResponse;
import com.akenzhan.taskmanagement.entity.Tag;
import com.akenzhan.taskmanagement.entity.Task;
import com.akenzhan.taskmanagement.entity.User;
import com.akenzhan.taskmanagement.enums.TaskPriority;
import com.akenzhan.taskmanagement.enums.TaskStatus;
import com.akenzhan.taskmanagement.exception.ResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.TaskMapper;
import com.akenzhan.taskmanagement.repository.TagRepository;
import com.akenzhan.taskmanagement.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectService projectService;
    private final UserService userService;
    private final TagRepository tagRepository;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository,
                       TaskMapper taskMapper,
                       ProjectService projectService,
                       UserService userService,
                       TagRepository tagRepository,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectService = projectService;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.notificationService = notificationService;
    }

    public TaskResponse createTask(TaskRequest request, String creatorUsername) {
        User creator = userService.getUserEntityByUsername(creatorUsername);
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .project(projectService.getProjectEntityById(request.getProjectId()))
                .creator(creator)
                .build();

        if (request.getAssigneeId() != null) {
            User assignee = userService.getUserEntityByUsername(
                    userService.getUserById(request.getAssigneeId()).getUsername());
            task.setAssignee(assignee);
            notificationService.sendTaskAssignmentNotification(assignee.getEmail(), task.getTitle());
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            task.setTags(tags);
        }

        Task saved = taskRepository.save(task);
        logger.info("Task created: {} by {}", saved.getTitle(), creatorUsername);
        return taskMapper.toResponse(saved);
    }

    public Page<TaskResponse> getTasks(TaskStatus status, TaskPriority priority,
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

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
        return taskMapper.toResponse(task);
    }

    public Task getTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = getTaskEntityById(id);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userService.getUserEntityByUsername(
                    userService.getUserById(request.getAssigneeId()).getUsername());
            task.setAssignee(assignee);
        }

        Task updated = taskRepository.save(task);
        logger.info("Task updated: {}", id);
        return taskMapper.toResponse(updated);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found: " + id);
        }
        taskRepository.deleteById(id);
        logger.info("Task deleted: {}", id);
    }
}
