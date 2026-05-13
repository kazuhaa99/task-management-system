package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.AkenzhanProjectRequest;
import com.akenzhan.taskmanagement.dto.response.AkenzhanProjectResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanProject;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import com.akenzhan.taskmanagement.exception.AkenzhanResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.AkenzhanProjectMapper;
import com.akenzhan.taskmanagement.repository.AkenzhanProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AkenzhanProjectService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanProjectService.class);

    private final AkenzhanProjectRepository projectRepository;
    private final AkenzhanProjectMapper projectMapper;
    private final AkenzhanUserService userService;

    public AkenzhanProjectService(AkenzhanProjectRepository projectRepository,
                                   AkenzhanProjectMapper projectMapper,
                                   AkenzhanUserService userService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
    }

    public AkenzhanProjectResponse createProject(AkenzhanProjectRequest request, String username) {
        AkenzhanUser owner = userService.getUserEntityByUsername(username);
        AkenzhanProject project = AkenzhanProject.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .active(true)
                .build();
        AkenzhanProject saved = projectRepository.save(project);
        logger.info("Project created: {} by {}", saved.getName(), username);
        return projectMapper.toResponse(saved);
    }

    public Page<AkenzhanProjectResponse> getAllProjects(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return projectRepository.findByNameContainingIgnoreCase(search, pageable).map(projectMapper::toResponse);
        }
        return projectRepository.findAll(pageable).map(projectMapper::toResponse);
    }

    public AkenzhanProjectResponse getProjectById(Long id) {
        AkenzhanProject project = projectRepository.findById(id)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("Project not found: " + id));
        return projectMapper.toResponse(project);
    }

    public AkenzhanProject getProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("Project not found: " + id));
    }

    public AkenzhanProjectResponse updateProject(Long id, AkenzhanProjectRequest request) {
        AkenzhanProject project = getProjectEntityById(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        AkenzhanProject updated = projectRepository.save(project);
        logger.info("Project updated: {}", id);
        return projectMapper.toResponse(updated);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new AkenzhanResourceNotFoundException("Project not found: " + id);
        }
        projectRepository.deleteById(id);
        logger.info("Project deleted: {}", id);
    }
}
