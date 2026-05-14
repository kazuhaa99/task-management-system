package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.enums.TaskStatus;
import com.akenzhan.taskmanagement.repository.AkenzhanTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class AkenzhanReportService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanReportService.class);

    private final AkenzhanTaskRepository taskRepository;
    private final AkenzhanNotificationService notificationService;

    public AkenzhanReportService(AkenzhanTaskRepository taskRepository,
                                  AkenzhanNotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generateProjectReport(Long projectId) {
        logger.info("[ASYNC] Generating report for project: {}", projectId);
        try {
            Thread.sleep(2000);
            long total = taskRepository.findByProjectId(projectId,
                    org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
            String report = String.format(
                "Report for project %d | Generated at: %s | Total tasks: %d",
                projectId, LocalDateTime.now(), total
            );
            logger.info("[ASYNC] Report generated: {}", report);
            return CompletableFuture.completedFuture(report);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkOverdueTasks() {
        logger.info("[SCHEDULED] Checking overdue tasks at {}", LocalDateTime.now());
        var overdueTasks = taskRepository.findByDueDateBeforeAndStatusNot(
                LocalDate.now(), TaskStatus.DONE);
        overdueTasks.forEach(task -> {
            logger.warn("[SCHEDULED] Overdue task: {} (due: {})", task.getTitle(), task.getDueDate());
            if (task.getAssignee() != null) {
                notificationService.sendDeadlineReminderNotification(
                        task.getAssignee().getEmail(), task.getTitle());
            }
        });
        logger.info("[SCHEDULED] Found {} overdue tasks", overdueTasks.size());
    }
}
