package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.response.AkenzhanAttachmentResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanAttachment;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import com.akenzhan.taskmanagement.exception.AkenzhanBadRequestException;
import com.akenzhan.taskmanagement.exception.AkenzhanResourceNotFoundException;
import com.akenzhan.taskmanagement.repository.AkenzhanAttachmentRepository;
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
public class AkenzhanFileService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanFileService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final AkenzhanAttachmentRepository attachmentRepository;
    private final AkenzhanTaskService taskService;
    private final AkenzhanUserService userService;

    public AkenzhanFileService(AkenzhanAttachmentRepository attachmentRepository,
                                AkenzhanTaskService taskService,
                                AkenzhanUserService userService) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    public AkenzhanAttachmentResponse uploadFile(Long taskId, MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new AkenzhanBadRequestException("File is empty");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            AkenzhanUser uploader = userService.getUserEntityByUsername(username);
            AkenzhanAttachment attachment = AkenzhanAttachment.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .task(taskService.getTaskEntityById(taskId))
                    .uploadedBy(uploader)
                    .build();

            AkenzhanAttachment saved = attachmentRepository.save(attachment);
            logger.info("File uploaded: {} for task: {}", file.getOriginalFilename(), taskId);
            return toResponse(saved);
        } catch (IOException e) {
            logger.error("File upload error: {}", e.getMessage());
            throw new AkenzhanBadRequestException("Could not upload file: " + e.getMessage());
        }
    }

    public Resource downloadFile(Long attachmentId) {
        AkenzhanAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("Attachment not found: " + attachmentId));
        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                logger.info("File downloaded: {}", attachment.getFileName());
                return resource;
            }
            throw new AkenzhanResourceNotFoundException("File not found on disk");
        } catch (MalformedURLException e) {
            throw new AkenzhanResourceNotFoundException("File not found: " + e.getMessage());
        }
    }

    public List<AkenzhanAttachmentResponse> getAttachmentsByTask(Long taskId) {
        return attachmentRepository.findByTaskId(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteAttachment(Long id) {
        AkenzhanAttachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("Attachment not found: " + id));
        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            logger.warn("Could not delete file from disk: {}", e.getMessage());
        }
        attachmentRepository.deleteById(id);
        logger.info("Attachment deleted: {}", id);
    }

    private AkenzhanAttachmentResponse toResponse(AkenzhanAttachment a) {
        AkenzhanAttachmentResponse r = new AkenzhanAttachmentResponse();
        r.setId(a.getId());
        r.setFileName(a.getFileName());
        r.setContentType(a.getContentType());
        r.setFileSize(a.getFileSize());
        r.setTaskId(a.getTask().getId());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
