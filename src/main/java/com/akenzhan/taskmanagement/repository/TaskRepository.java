package com.akenzhan.taskmanagement.repository;

import com.akenzhan.taskmanagement.entity.Task;
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
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

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
    Page<Task> findWithFilters(
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("projectId") Long projectId,
            @Param("assigneeId") Long assigneeId,
            @Param("search") String search,
            Pageable pageable);

    List<Task> findByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);
}