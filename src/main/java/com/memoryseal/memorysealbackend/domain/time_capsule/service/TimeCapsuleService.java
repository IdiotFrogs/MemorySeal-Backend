package com.memoryseal.memorysealbackend.service;

import com.memoryseal.memorysealbackend.dto.*;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.repository.TimeCapsuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .timeCapsuleActiveStatus(timeCapsuleCreateDto.getTimeCapsuleActiveStatus())
                .userId(timeCapsuleCreateDto.getUserId())
                .build();
        return timeCapsuleJpaRepository.save(timeCapsule);
    }

    public TimeCapsuleResponseDto getDetail(Long id) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        return TimeCapsuleResponseDto.toDto(timeCapsule);
    }

    public TimeCapsuleUpdateDto updateTimeCapsule(Long id, TimeCapsuleUpdateDto timeCapsuleUpdateDto) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        if(timeCapsuleUpdateDto.getTitle() != null) {
            timeCapsule.setTitle(timeCapsuleUpdateDto.getTitle());
        }
        if(timeCapsuleUpdateDto.getBuriedAt() != null) {
            timeCapsule.setBuriedAt(timeCapsuleUpdateDto.getBuriedAt());
        }
        if(timeCapsuleUpdateDto.getOpenedAt() != null) {
            timeCapsule.setOpenedAt(timeCapsuleUpdateDto.getOpenedAt());
        }
        if(timeCapsuleUpdateDto.getTimeCapsuleStatus() != null) {
            timeCapsule.setTimeCapsuleStatus(timeCapsuleUpdateDto.getTimeCapsuleStatus());
        }

        timeCapsule.setUpdatedAt(LocalDateTime.now());

        timeCapsuleJpaRepository.save(timeCapsule);

        return TimeCapsuleUpdateDto.toDto(timeCapsule);
    }


}
