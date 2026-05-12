package com.akenzhan.taskmanagement.repository;

import com.akenzhan.taskmanagement.entity.AkenzhanComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AkenzhanCommentRepository extends JpaRepository<AkenzhanComment, Long> {
    Page<AkenzhanComment> findByTaskId(Long taskId, Pageable pageable);
}
