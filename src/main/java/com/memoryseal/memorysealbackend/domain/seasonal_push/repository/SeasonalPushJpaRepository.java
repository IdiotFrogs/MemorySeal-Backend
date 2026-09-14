package com.memoryseal.memorysealbackend.domain.seasonal_push.repository;

import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.SeasonalPush;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonalPushJpaRepository extends JpaRepository<SeasonalPush, Long> {

}
