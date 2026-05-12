package com.akenzhan.taskmanagement.repository;

import com.akenzhan.taskmanagement.entity.AkenzhanProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AkenzhanProjectRepository extends JpaRepository<AkenzhanProject, Long> {
    Page<AkenzhanProject> findByOwnerId(Long ownerId, Pageable pageable);
    List<AkenzhanProject> findByActiveTrue();

    @Query("SELECT p FROM AkenzhanProject p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<AkenzhanProject> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
