package com.memoryseal.memorysealbackend.domain.time_capsule.repository;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleWatering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WateringJpaRepository extends JpaRepository<TimeCapsuleWatering, Long> {
    boolean existsByTimeCapsuleIdAndWateredDate(Long capsuleId, LocalDate wateredDate);
    List<TimeCapsuleWatering> findByTimeCapsuleId(Long capsuleId);
    Long countByTimeCapsuleId(Long capsuleId);
    @Query("SELECT w.timeCapsuleId, COUNT(w) FROM TimeCapsuleWatering w WHERE w.timeCapsuleId IN :capsuleIds GROUP BY w.timeCapsuleId")
    List<Object[]> countByTimeCapsuleIdIn(@Param("capsuleIds") List<Long> capsuleIds);
}
