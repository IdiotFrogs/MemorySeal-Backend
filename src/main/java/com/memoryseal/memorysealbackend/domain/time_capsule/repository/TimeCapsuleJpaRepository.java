package com.memoryseal.memorysealbackend.domain.time_capsule.repository;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeCapsuleJpaRepository extends JpaRepository<TimeCapsule, Long> {

    List<TimeCapsule> findByOpenedAtAndTimeCapsuleStatus(
            LocalDate openedAt,
            TimeCapsuleStatus status
    );

    List<TimeCapsule> findByIdInAndTimeCapsuleStatus(
            List<Long> ids,
            TimeCapsuleStatus timeCapsuleStatus
    );

    @Query("SELECT t FROM TimeCapsule t " + "WHERE  t.openedAt BETWEEN :start AND :end")
    List<TimeCapsule> findOpenedInSeason(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
