package com.akenzhan.taskmanagement.repository;

import com.akenzhan.taskmanagement.entity.AkenzhanAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AkenzhanAttachmentRepository extends JpaRepository<AkenzhanAttachment, Long> {
    List<AkenzhanAttachment> findByTaskId(Long taskId);
}
