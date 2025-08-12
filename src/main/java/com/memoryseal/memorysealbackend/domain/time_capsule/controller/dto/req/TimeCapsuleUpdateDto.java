package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req;

import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeCapsuleUpdateDto {

    private String title;

    private LocalDateTime buriedAt;

    private LocalDateTime openedAt;

    private TimeCapsuleStatus timeCapsuleStatus;

    private LocalDateTime updatedAt;

    public static TimeCapsuleUpdateDto toDto(TimeCapsule timeCapsule) {
        if (timeCapsule == null) {
            return null;
        }else {
            return TimeCapsuleUpdateDto.builder()
                    .title(timeCapsule.getTitle())
                    .buriedAt(timeCapsule.getBuriedAt())
                    .openedAt(timeCapsule.getOpenedAt())
                    .timeCapsuleStatus(timeCapsule.getTimeCapsuleStatus())
                    .updatedAt(timeCapsule.getUpdatedAt())
                    .build();
        }
    }

}
