package com.memoryseal.memorysealbackend.service;

import com.memoryseal.memorysealbackend.controller.TimeCapsuleController;
import com.memoryseal.memorysealbackend.dto.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.repository.TimeCapsuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeCapsuleService {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;

    public TimeCapsule createTimeCapsule(TimeCapsuleCreateDto timeCapsuleCreateDto) {
        TimeCapsule timeCapsule = TimeCapsule.builder()
                .title(timeCapsuleCreateDto.getTitle())
                .buriedAt(timeCapsuleCreateDto.getBuriedAt())
                .openedAt(timeCapsuleCreateDto.getOpenedAt())
                .timeCapsuleStatus(timeCapsuleCreateDto.getTimeCapsuleStatus())
                .createdAt(timeCapsuleCreateDto.getCreatedAt())
                .updatedAt(timeCapsuleCreateDto.getUpdatedAt())
                .timeCapsuleActiveStatus(timeCapsuleCreateDto.getTimeCapsuleActiveStatus())
                .build();
        return timeCapsuleJpaRepository.save(timeCapsule);
    }
}
