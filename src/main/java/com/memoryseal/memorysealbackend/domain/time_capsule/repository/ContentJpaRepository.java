package com.memoryseal.memorysealbackend.domain.time_capsule.repository;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentJpaRepository extends JpaRepository<TimeCapsuleContent, Long> {
}
