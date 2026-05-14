package com.akenzhan.taskmanagement.controller;

import com.akenzhan.taskmanagement.dto.response.AkenzhanAttachmentResponse;
import com.akenzhan.taskmanagement.service.AkenzhanFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/files")
@Tag(name = "Files", description = "File upload and download endpoints")
public class AkenzhanFileController {

    private final AkenzhanFileService fileService;

    public AkenzhanFileController(AkenzhanFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file to task")
    public ResponseEntity<AkenzhanAttachmentResponse> uploadFile(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        return ResponseEntity.ok(fileService.uploadFile(taskId, file, principal.getName()));
    }

    @GetMapping
    @Operation(summary = "Get all attachments for task")
    public ResponseEntity<List<AkenzhanAttachmentResponse>> getAttachments(@PathVariable Long taskId) {
        return ResponseEntity.ok(fileService.getAttachmentsByTask(taskId));
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Download file by attachment ID")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId) {
        Resource resource = fileService.downloadFile(attachmentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId) {
        fileService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
