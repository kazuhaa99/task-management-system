package com.akenzhan.taskmanagement.controller;

import com.akenzhan.taskmanagement.dto.request.AkenzhanProjectRequest;
import com.akenzhan.taskmanagement.dto.response.AkenzhanProjectResponse;
import com.akenzhan.taskmanagement.service.AkenzhanProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management endpoints")
public class AkenzhanProjectController {

    private final AkenzhanProjectService projectService;

    public AkenzhanProjectController(AkenzhanProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<AkenzhanProjectResponse> createProject(
            @Valid @RequestBody AkenzhanProjectRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request, principal.getName()));
    }

    @GetMapping
    @Operation(summary = "Get all projects with search, pagination and sorting")
    public ResponseEntity<Page<AkenzhanProjectResponse>> getAllProjects(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(projectService.getAllProjects(search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<AkenzhanProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    public ResponseEntity<AkenzhanProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody AkenzhanProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
