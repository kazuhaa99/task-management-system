package com.akenzhan.taskmanagement.controller;

import com.akenzhan.taskmanagement.dto.request.TaskRequest;
import com.akenzhan.taskmanagement.dto.response.TaskResponse;
import com.akenzhan.taskmanagement.enums.TaskPriority;
import com.akenzhan.taskmanagement.enums.TaskStatus;
import com.akenzhan.taskmanagement.service.ReportService;
import com.akenzhan.taskmanagement.service.TaskService;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;
    private final ReportService reportService;

    public TaskController(TaskService taskService, ReportService reportService) {
        this.taskService = taskService;
        this.reportService = reportService;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, principal.getName()));
    }

    @GetMapping
    @Operation(summary = "Get tasks with filters, search, pagination and sorting")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(status, priority, projectId, assigneeId, search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}/report")
    @Operation(summary = "Generate async report for project")
    public CompletableFuture<ResponseEntity<String>> generateReport(@PathVariable Long projectId) {
        return reportService.generateProjectReport(projectId)
                .thenApply(ResponseEntity::ok);
    }
}
