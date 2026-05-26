package com.memoryseal.memorysealbackend.domain.file.repository;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachedFileJpaRepository extends JpaRepository<AttachedFile, Long> {
    List<AttachedFile> findByTimeCapsuleContentIdIn(List<Long> contentIds);
}
