package com.memoryseal.memorysealbackend.domain.time_capsule.repository;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeCapsuleJpaRepository extends JpaRepository<TimeCapsule, Long> {
    List<TimeCapsule> findByUserId(Long userId);

}
