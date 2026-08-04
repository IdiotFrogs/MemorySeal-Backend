package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WateringDto {
    private LocalDate wateredDate;
    private Boolean isWatered;
    private Long userId;
    private String profileImageUrl;
}
