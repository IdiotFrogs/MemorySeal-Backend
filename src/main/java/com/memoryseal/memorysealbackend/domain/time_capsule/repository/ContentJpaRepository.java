package com.memoryseal.memorysealbackend.domain.time_capsule.repository;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentJpaRepository extends JpaRepository<TimeCapsuleContent, Long> {
    List<TimeCapsuleContent> findByTimeCapsuleId(Long timeCapsuleId);

    List<TimeCapsuleContent> findByTimeCapsuleIdAndUserId(Long timeCapsuleId, Long userId);

    List<TimeCapsuleContent> findByTimeCapsuleIdAndUserIdIn(Long timeCapsuleId, List<Long> userIds);

    void deleteByIdAndUserId(Long id, Long userId);
}
