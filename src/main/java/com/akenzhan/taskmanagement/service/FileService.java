package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.response.AttachmentResponse;
import com.akenzhan.taskmanagement.entity.Attachment;
import com.akenzhan.taskmanagement.entity.User;
import com.akenzhan.taskmanagement.exception.BadRequestException;
import com.akenzhan.taskmanagement.exception.ResourceNotFoundException;
import com.akenzhan.taskmanagement.repository.AttachmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final AttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final UserService userService;

    public FileService(AttachmentRepository attachmentRepository,
                       TaskService taskService,
                       UserService userService) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    public AttachmentResponse uploadFile(Long taskId, MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            User uploader = userService.getUserEntityByUsername(username);
            Attachment attachment = Attachment.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .task(taskService.getTaskEntityById(taskId))
                    .uploadedBy(uploader)
                    .build();

            Attachment saved = attachmentRepository.save(attachment);
            logger.info("File uploaded: {} for task: {}", file.getOriginalFilename(), taskId);
            return toResponse(saved);
        } catch (IOException e) {
            logger.error("File upload error: {}", e.getMessage());
            throw new BadRequestException("Could not upload file: " + e.getMessage());
        }
    }

    public Resource downloadFile(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found: " + attachmentId));
        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                logger.info("File downloaded: {}", attachment.getFileName());
                return resource;
            }
            throw new ResourceNotFoundException("File not found on disk");
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + e.getMessage());
        }
    }

    public List<AttachmentResponse> getAttachmentsByTask(Long taskId) {
        return attachmentRepository.findByTaskId(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found: " + id));
        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            logger.warn("Could not delete file from disk: {}", e.getMessage());
        }
        attachmentRepository.deleteById(id);
        logger.info("Attachment deleted: {}", id);
    }

    private AttachmentResponse toResponse(Attachment a) {
        AttachmentResponse r = new AttachmentResponse();
        r.setId(a.getId());
        r.setFileName(a.getFileName());
        r.setContentType(a.getContentType());
        r.setFileSize(a.getFileSize());
        r.setTaskId(a.getTask().getId());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
