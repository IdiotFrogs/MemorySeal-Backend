package com.memoryseal.memorysealbackend.dto;

import com.memoryseal.memorysealbackend.enums.TimeCapsuleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class TimeCapsuleCreateDto {

    private String title;

    private LocalDateTime buriedAt;

    private LocalDateTime openedAt;

    private TimeCapsuleStatus timeCapsuleStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean timeCapsuleActiveStatus;

}
