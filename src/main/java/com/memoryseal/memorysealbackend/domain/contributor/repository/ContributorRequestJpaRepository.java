package com.memoryseal.memorysealbackend.domain.contributor.repository;

import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContributorRequestJpaRepository extends JpaRepository<ContributorRequest, Long> {
    Optional<ContributorRequest> findByUserIdAndTimeCapsuleId(Long userId, Long timeCapsuleId);
}
