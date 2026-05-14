package com.akenzhan.taskmanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AkenzhanNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanNotificationService.class);

    @Async("taskExecutor")
    public void sendTaskAssignmentNotification(String email, String taskTitle) {
        try {
            Thread.sleep(1000);
            logger.info("[ASYNC] Notification sent to {} for task: {}", email, taskTitle);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Notification interrupted", e);
        }
    }

    @Async("taskExecutor")
    public void sendDeadlineReminderNotification(String email, String taskTitle) {
        try {
            Thread.sleep(500);
            logger.info("[ASYNC] Deadline reminder sent to {} for task: {}", email, taskTitle);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Reminder interrupted", e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<String> sendWelcomeNotification(String email, String username) {
        try {
            Thread.sleep(800);
            String message = "Welcome email sent to: " + email;
            logger.info("[ASYNC] {}", message);
            return CompletableFuture.completedFuture(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
}
