package com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.PageResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WateringResponseDto {
    private long totalDays;
    private long wateringCount;
    private int stage;
    private PageResponseDto<WateringDto> waterings;
}
