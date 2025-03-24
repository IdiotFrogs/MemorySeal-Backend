package com.memoryseal.memorysealbackend.repository;

import com.memoryseal.memorysealbackend.entity.TimeCapsule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeCapsuleJpaRepository extends JpaRepository<TimeCapsule, Long> {
}
