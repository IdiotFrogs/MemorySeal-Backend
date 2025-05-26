package com.memoryseal.memorysealbackend.repository;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributorJpaRepository extends JpaRepository<Contributor, Long> {
    List<Contributor> findByTimeCapsuleId(Long TimeCapsuleId);
}
