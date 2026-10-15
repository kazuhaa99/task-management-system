package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.ProjectRequest;
import com.akenzhan.taskmanagement.dto.response.ProjectResponse;
import com.akenzhan.taskmanagement.entity.Project;
import com.akenzhan.taskmanagement.entity.User;
import com.akenzhan.taskmanagement.exception.ResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.ProjectMapper;
import com.akenzhan.taskmanagement.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMapper projectMapper,
                          UserService userService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
    }

    public ProjectResponse createProject(ProjectRequest request, String username) {
        User owner = userService.getUserEntityByUsername(username);
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .active(true)
                .build();
        Project saved = projectRepository.save(project);
        logger.info("Project created: {} by {}", saved.getName(), username);
        return projectMapper.toResponse(saved);
    }

    public Page<ProjectResponse> getAllProjects(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return projectRepository.findByNameContainingIgnoreCase(search, pageable).map(projectMapper::toResponse);
        }
        return projectRepository.findAll(pageable).map(projectMapper::toResponse);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
        return projectMapper.toResponse(project);
    }

    public Project getProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = getProjectEntityById(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        Project updated = projectRepository.save(project);
        logger.info("Project updated: {}", id);
        return projectMapper.toResponse(updated);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }
        projectRepository.deleteById(id);
        logger.info("Project deleted: {}", id);
    }
}
