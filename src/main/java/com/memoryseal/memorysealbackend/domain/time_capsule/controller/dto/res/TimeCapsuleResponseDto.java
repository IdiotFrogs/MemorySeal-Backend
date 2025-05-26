package com.memoryseal.memorysealbackend.dto;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeCapsuleResponseDto {

    private String title;

    private LocalDateTime buriedAt;

    private LocalDateTime openedAt;

    private Boolean timeCapsuleActiveStatus;

    public static TimeCapsuleResponseDto toDto(TimeCapsule timeCapsule) {
        if (timeCapsule == null) {
            return null;
        }else {
            return TimeCapsuleResponseDto.builder()
                    .title(timeCapsule.getTitle())
                    .buriedAt(timeCapsule.getBuriedAt())
                    .openedAt(timeCapsule.getOpenedAt())
                    .timeCapsuleActiveStatus(timeCapsule.getTimeCapsuleActiveStatus())
                    .build();
        }
    }
}
