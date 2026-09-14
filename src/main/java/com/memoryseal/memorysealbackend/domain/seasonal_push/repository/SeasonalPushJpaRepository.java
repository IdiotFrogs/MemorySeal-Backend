package com.memoryseal.memorysealbackend.domain.seasonal_push.repository;

import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.Season;
import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.SeasonalPush;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SeasonalPushJpaRepository extends JpaRepository<SeasonalPush, Long> {
    @Query("SELECT p.userId FROM SeasonalPush p " + "WHERE p.season = :season AND p.year = :year")
    Set<Long> findAlreadySentUserIds(@Param("season") Season season,
                                     @Param("year") Integer year);

    Optional<SeasonalPush> findByUserIdAndSeasonAndYear(Long userId, Season season, int year);
}
