package com.akenzhan.taskmanagement.repository;

import com.akenzhan.taskmanagement.entity.AkenzhanTask;
import com.akenzhan.taskmanagement.enums.TaskPriority;
import com.akenzhan.taskmanagement.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AkenzhanTaskRepository extends JpaRepository<AkenzhanTask, Long> {

    Page<AkenzhanTask> findByProjectId(Long projectId, Pageable pageable);

    Page<AkenzhanTask> findByAssigneeId(Long assigneeId, Pageable pageable);

    Page<AkenzhanTask> findByStatus(TaskStatus status, Pageable pageable);

    Page<AkenzhanTask> findByPriority(TaskPriority priority, Pageable pageable);

    @Query(value = "SELECT * FROM tasks t WHERE " +
       "(:status IS NULL OR t.status = :status) AND " +
       "(:priority IS NULL OR t.priority = :priority) AND " +
       "(:projectId IS NULL OR t.project_id = :projectId) AND " +
       "(:assigneeId IS NULL OR t.assignee_id = :assigneeId) AND " +
       "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))",
       countQuery = "SELECT COUNT(*) FROM tasks t WHERE " +
       "(:status IS NULL OR t.status = :status) AND " +
       "(:priority IS NULL OR t.priority = :priority) AND " +
       "(:projectId IS NULL OR t.project_id = :projectId) AND " +
       "(:assigneeId IS NULL OR t.assignee_id = :assigneeId) AND " +
       "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))",
       nativeQuery = true)
    Page<AkenzhanTask> findWithFilters(
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("projectId") Long projectId,
            @Param("assigneeId") Long assigneeId,
            @Param("search") String search,
            Pageable pageable);

    List<AkenzhanTask> findByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);
}