package com.akenzhan.taskmanagement.repository;

import com.akenzhan.taskmanagement.entity.AkenzhanTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AkenzhanTagRepository extends JpaRepository<AkenzhanTag, Long> {
    Optional<AkenzhanTag> findByName(String name);
    boolean existsByName(String name);
}
